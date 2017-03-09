/*
 * ============================================================================
 *
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */

package org.talend.daikon.version.events.javers;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.object.GlobalId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.version.api.ModificationsProvider;
import org.talend.daikon.version.events.ChangeType;
import org.talend.daikon.version.events.FieldModification;

/**
 * An implementation of {@link ModificationsProvider} based on
 * <a href="http://javers.org/">Javers</a>.
 */
public class JaversModificationsProvider implements ModificationsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JaversModificationsProvider.class);

    private final Javers javers;

    public JaversModificationsProvider() {
        this(JaversBuilder.javers());
    }

    public JaversModificationsProvider(JaversBuilder javersBuilder) {
        this.javers = javersBuilder.withNewObjectsSnapshot(true).build();
    }

    @Override
    public <T> List<FieldModification> getModifications(T v1, T v2) {
        Diff result = this.javers.compare(v1, v2);
        DiffContext context = new DiffContext();
        List<FieldModification> modifications = adaptDiff(result, context);
        modifications.sort(Comparator.comparing(FieldModification::getPath));
        return modifications;
    }

    private List<FieldModification> adaptDiff(Diff result, DiffContext context) {
        List<FieldModification> modifications = new ArrayList<>();
        for (Change change : result.getChanges()) {
            List<FieldModification> modification = this.adaptChange(change, context);
            if (modification != null) {
                modifications.addAll(modification);
            }
        }
        return modifications;
    }

    private List<FieldModification> adaptChange(Change change, DiffContext context) {
        if (change instanceof PropertyChange) {
            PropertyChange propertyChange = (PropertyChange) change;
            return this.adaptPropertyChange(propertyChange, context);
        } else if (change instanceof ObjectRemoved) {
            ObjectRemoved objectRemoved = (ObjectRemoved) change;
            final Optional<Object> affectedObject = objectRemoved.getAffectedObject();
            if (affectedObject.isPresent()) {
                Diff initial = this.javers.initial(affectedObject.get());
                context.addDeletedObject(objectRemoved.getAffectedGlobalId(), initial);
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }

    private List<FieldModification> adaptPropertyChange(PropertyChange propertyChange, DiffContext context) {
        if (propertyChange instanceof ValueChange) {
            return this.adaptValueChange((ValueChange) propertyChange);
        } else if (propertyChange instanceof ReferenceChange) {
            return this.adaptReferenceChange((ReferenceChange) propertyChange);
        } else if (propertyChange instanceof MapChange) {
            return this.adaptMapChange((MapChange) propertyChange);
        } else if (propertyChange instanceof ArrayChange) {
            return this.adaptArrayChange((ArrayChange) propertyChange, context);
        } else if (propertyChange instanceof CollectionChange) {
            return this.adaptCollectionChange((CollectionChange) propertyChange, context);
        }
        return Collections.emptyList();
    }

    private List<FieldModification> adaptReferenceChange(ReferenceChange referenceChange) {
        Object left = null;
        Object right = null;
        final Optional<Object> leftObject = referenceChange.getLeftObject();
        if (leftObject.isPresent()) {
            left = leftObject.get();
        }
        final Optional<Object> rightObject = referenceChange.getRightObject();
        if (rightObject.isPresent()) {
            right = rightObject.get();
        }
        if (left == null && right == null) {
            return Collections.emptyList();
        }
        if (left == null) {
            return Collections.emptyList();
        }
        if (right == null) {
            right = newInstance(left.getClass());
        }
        String path = propertyToPath(referenceChange.getAffectedGlobalId(), referenceChange.getPropertyName());
        List<FieldModification> result = this.getModifications(left, right);
        for (FieldModification fieldModification : result) {
            fieldModification.setPath(path + FieldModification.PATH_PARTS_SEPARATOR + fieldModification.getPath());
        }
        return result;
    }

    private Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not instantiate class " + clazz.getName(), e);
        }
    }

    private List<FieldModification> adaptCollectionChange(CollectionChange propertyChange, DiffContext context) {
        if (propertyChange instanceof ListChange) {
            return this.adaptListChange((ListChange) propertyChange, context);
        } else if (propertyChange instanceof SetChange) {
            throw new UnsupportedOperationException("Sets are not supported yet");
        } else {
            throw new IllegalArgumentException("Unsupported CollectionChange type " + propertyChange);
        }
    }

    private List<FieldModification> adaptListChange(ListChange listChange, DiffContext context) {
        String path = this.propertyToPath(listChange.getAffectedGlobalId(), listChange.getPropertyName());
        List<FieldModification> result = new ArrayList<>();
        for (ContainerElementChange change : listChange.getChanges()) {
            List<FieldModification> modification = this.adaptContainerElementChange(path, change, context);
            if (modification != null) {
                result.addAll(modification);
            }
        }
        return result;
    }

    private List<FieldModification> adaptContainerElementChange(String path, ContainerElementChange change, DiffContext context) {
        String arrayPath = path + FieldModification.ARRAY_INDEX_START + change.getIndex() + FieldModification.ARRAY_INDEX_END;
        if (change instanceof ValueAdded) {
            ValueAdded valueAdded = (ValueAdded) change;
            Object addedValue = valueAdded.getAddedValue();
            if (addedValue instanceof GlobalId) {
                return Collections.emptyList();
            }
            return Collections.singletonList(FieldModification.createFieldCreation(arrayPath, addedValue));
        } else if (change instanceof ValueRemoved) {
            List<FieldModification> result = new ArrayList<>();
            ValueRemoved valueRemoved = (ValueRemoved) change;
            Object o = valueRemoved.getRemovedValue();
            if (o instanceof GlobalId) {
                Diff deletedObject = context.getDeletedObject((GlobalId) o);
                result.add(FieldModification.createFieldDeletion(arrayPath, null));
                if (deletedObject != null) {
                    List<FieldModification> modifications = this.adaptDiff(deletedObject, context);
                    modifications.forEach(mod -> {
                        mod.setPath(arrayPath + FieldModification.PATH_PARTS_SEPARATOR + mod.getPath());
                        mod.setType(ChangeType.DELETE);
                        mod.setPreviousValue(mod.getNewValue());
                        mod.setNewValue(null);
                    });
                    result.addAll(modifications);
                }
                return result;
            } else {
                return Collections.singletonList(FieldModification.createFieldDeletion(arrayPath, o));
            }
        } else if (change instanceof ElementValueChange) {
            ElementValueChange elementValueChange = (ElementValueChange) change;
            return Collections.singletonList(FieldModification.createFieldUpdate(arrayPath, elementValueChange.getLeftValue(),
                    elementValueChange.getRightValue()));
        } else {
            throw new IllegalArgumentException("Unsupported container element change " + change);
        }
    }

    private List<FieldModification> adaptMapChange(MapChange mapChange) {
        String path = this.propertyToPath(mapChange.getAffectedGlobalId(), mapChange.getPropertyName());
        return mapChange.getEntryChanges().stream().map(entryChange -> this.adaptEntryChange(path, entryChange))
                .collect(Collectors.toList());
    }

    private FieldModification adaptEntryChange(String path, EntryChange entryChange) {
        if (entryChange instanceof EntryAdded) {
            return this.adaptEntryAdded(path, (EntryAdded) entryChange);
        } else if (entryChange instanceof EntryRemoved) {
            return this.adaptEntryRemoved(path, (EntryRemoved) entryChange);
        } else if (entryChange instanceof EntryValueChange) {
            return this.adaptEntryValueChanged(path, (EntryValueChange) entryChange);
        } else {
            throw new IllegalArgumentException("Unsupported EntryChange subtype: " + entryChange);
        }
    }

    private FieldModification adaptEntryAdded(String path, EntryAdded entryAdded) {
        return FieldModification.createFieldCreation(path + FieldModification.PATH_PARTS_SEPARATOR + entryAdded.getKey(),
                entryAdded.getValue());
    }

    private FieldModification adaptEntryRemoved(String path, EntryRemoved entryRemoved) {
        return FieldModification.createFieldDeletion(path + FieldModification.PATH_PARTS_SEPARATOR + entryRemoved.getKey(),
                entryRemoved.getValue());
    }

    private FieldModification adaptEntryValueChanged(String path, EntryValueChange entryValueChange) {
        return FieldModification.createFieldUpdate(path + FieldModification.PATH_PARTS_SEPARATOR + entryValueChange.getKey(),
                entryValueChange.getLeftValue(), entryValueChange.getRightValue());
    }

    private List<FieldModification> adaptArrayChange(ArrayChange arrayChange, DiffContext context) {
        String path = this.propertyToPath(arrayChange.getAffectedGlobalId(), arrayChange.getPropertyName());
        List<FieldModification> result = new ArrayList<>();
        for (ContainerElementChange change : arrayChange.getChanges()) {
            List<FieldModification> modification = this.adaptContainerElementChange(path, change, context);
            if (modification != null) {
                result.addAll(modification);
            }
        }
        return result;
    }

    private List<FieldModification> adaptValueChange(ValueChange valueChange) {
        String path = this.propertyToPath(valueChange.getAffectedGlobalId(), valueChange.getPropertyName());
        return Collections
                .singletonList(FieldModification.createFieldUpdate(path, valueChange.getLeft(), valueChange.getRight()));
    }

    private String propertyToPath(GlobalId globalId, String propertyName) {
        String path = globalIdToPath(globalId);
        if (!StringUtils.isEmpty(path)) {
            return path + FieldModification.PATH_PARTS_SEPARATOR + propertyName;
        }
        return propertyName;
    }

    private String globalIdToPath(GlobalId globalId) {
        return globalIdValueToPath(globalId.value());
    }

    private String globalIdValueToPath(String globalIdValue) {
        StringTokenizer tokenizer = new StringTokenizer(globalIdValue, "#/", false);
        StringBuilder builder = new StringBuilder();
        short index = 0;
        while (tokenizer.hasMoreElements()) {
            // skip the 2 first elements (ref to object class and object id)
            if (index < 2) {
                tokenizer.nextToken();
            } else {
                String part = tokenizer.nextToken();
                boolean isArray = isArray(part);
                if (index >= 3 && !isArray) {
                    builder.append(FieldModification.PATH_PARTS_SEPARATOR);
                }
                if (isArray) {
                    part = FieldModification.ARRAY_INDEX_START + part + FieldModification.ARRAY_INDEX_END;
                }
                builder.append(part);
            }
            index++;
        }
        return builder.toString();
    }

    private boolean isArray(String part) {
        try {
            Integer.parseInt(part);
            return true;
        } catch (Exception e) {
            LOG.debug("Exception when trying parse integer value {}", part, e);
            return false;
        }
    }

    private static class DiffContext {

        private Map<String, Diff> deletedObjects = new HashMap<>();

        private void addDeletedObject(GlobalId id, Diff diff) {
            this.deletedObjects.put(id.toString(), diff);
        }

        private Diff getDeletedObject(GlobalId id) {
            return this.deletedObjects.get(id.toString());
        }

    }
}
