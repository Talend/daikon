/**
 * This package supports persistence of Java objects using a JSON string.
 * 
 * <h2>Requirements</h2>
 * <ol>
 * <li>Persistent metadata saved in an older version must be completely usable in a current version (there will be a
 * limit on which older versions are supported).
 * <li>If older metadata is detected, the user can optionally be notified that a migration took place so the data can be
 * resaved in the current version.
 * <li>If possible, metadata in a newer version should work correctly in older versions. But this may not always be
 * possible.
 * </ol>
 * 
 * <h2>Assumptions</h2>
 * <p>
 * These notes assume the metadata is implemented in ComponentProperties subclasses (soon to be an implementation of the
 * generic Properties service that will be put in Daikon).
 * 
 * <p>
 * The serialization is at the Java class level; each class manages its properties/fields and manages the migration
 * issues related to changes. The class is completely self-contained to allow for reuse. There is no separate mechanism
 * (outside of the class) that handles migration.
 * 
 * <p>
 * The persistent format is JSON as generated by the https://github.com/jdereg/json-io code. This was selected because
 * it's able to transparently and automatically serialize and deserialize Java objects comprised of true object graphs
 * (with cycles) without any special annotations. As of this writing, this is the only JSON package that does this. If
 * this sort of feature gets added to Jackson (for example), then we can consider migrating to that.
 * 
 * <h2>Possible Changes</h2> The possible changes to the class are:
 * 
 * <ol>
 * <li>Add a field/property.
 * <li>Remove a field/property.
 * <li>Change the semantic meaning of a field/property - which could result in the change of the value during a
 * migration. Should be rare, but possible. One example of this is the the property value was set incorrectly due to a
 * bug and is set correctly (and assumed to be correct) in a future version. The value from the older versions needs to
 * be fixed.
 * <li>Change the datatype of a field/property - rare and similar to the above.
 * </ol>
 * 
 * <h2>Implementation</h2>
 * <p>
 * 
 * FIXME - finish this.
 * <p>
 * Here are the rules and techniques for handling the possible changes:
 * 
 * <ol>
 * <li>Add - No special handling is required. Older software versions will be able to read newer objects without
 * problems. The deserialization mechanism will make sure that unknown fields are ignored.
 * <li>Remove - The version in which the field is removed is obliged to provide a method that handles an older version
 * that provides the field, and sets the current object to respond to that in a way that's appropriate for the
 * migration. This method is automatically called by the JSON deserializer, and given the field value.
 * <li>Change Meaning - Similar to the remove case, except the given method will take the old field value and return the
 * new field value.
 * <li>Change datatype - Same as above.
 * </ol>
 */
package org.talend.daikon.persistence;