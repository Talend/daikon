package org.talend.daikon.content.s3;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.S3ResponseMetadata;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;

public class AmazonS3TestWrapper implements AmazonS3 {

    private final AmazonS3 delegate;

    public AmazonS3TestWrapper(AmazonS3 delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEndpoint(String endpoint) {
        delegate.setEndpoint(endpoint);
    }

    @Override
    public void setRegion(com.amazonaws.regions.Region region) throws IllegalArgumentException {
        delegate.setRegion(region);
    }

    @Override
    public void setS3ClientOptions(S3ClientOptions clientOptions) {
        delegate.setS3ClientOptions(clientOptions);
    }

    @Override
    @Deprecated
    public void changeObjectStorageClass(String bucketName, String key, StorageClass newStorageClass)
            throws AmazonClientException {
        delegate.changeObjectStorageClass(bucketName, key, newStorageClass);
    }

    @Override
    @Deprecated
    public void setObjectRedirectLocation(String bucketName, String key, String newRedirectLocation)
            throws AmazonClientException {
        delegate.setObjectRedirectLocation(bucketName, key, newRedirectLocation);
    }

    @Override
    public ObjectListing listObjects(String bucketName) throws AmazonClientException {
        return delegate.listObjects(bucketName);
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix) throws AmazonClientException {
        return delegate.listObjects(bucketName, prefix);
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws AmazonClientException {
        return delegate.listObjects(listObjectsRequest);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName) throws AmazonClientException {
        return delegate.listObjectsV2(bucketName);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName, String prefix) throws AmazonClientException {
        return delegate.listObjectsV2(bucketName, prefix);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws AmazonClientException {
        return delegate.listObjectsV2(listObjectsV2Request);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ObjectListing previousObjectListing) throws AmazonClientException {
        return delegate.listNextBatchOfObjects(previousObjectListing);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ListNextBatchOfObjectsRequest listNextBatchOfObjectsRequest)
            throws AmazonClientException {
        return delegate.listNextBatchOfObjects(listNextBatchOfObjectsRequest);
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix) throws AmazonClientException {
        return delegate.listVersions(bucketName, prefix);
    }

    @Override
    public VersionListing listNextBatchOfVersions(VersionListing previousVersionListing) throws AmazonClientException {
        return delegate.listNextBatchOfVersions(previousVersionListing);
    }

    @Override
    public VersionListing listNextBatchOfVersions(ListNextBatchOfVersionsRequest listNextBatchOfVersionsRequest)
            throws AmazonClientException {
        return delegate.listNextBatchOfVersions(listNextBatchOfVersionsRequest);
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix, String keyMarker, String versionIdMarker,
            String delimiter, Integer maxResults) throws AmazonClientException {
        return delegate.listVersions(bucketName, prefix, keyMarker, versionIdMarker, delimiter, maxResults);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws AmazonClientException {
        return delegate.listVersions(listVersionsRequest);
    }

    @Override
    public Owner getS3AccountOwner() throws AmazonClientException {
        return delegate.getS3AccountOwner();
    }

    @Override
    public Owner getS3AccountOwner(GetS3AccountOwnerRequest getS3AccountOwnerRequest) throws AmazonClientException {
        return delegate.getS3AccountOwner(getS3AccountOwnerRequest);
    }

    @Deprecated
    @Override
    public boolean doesBucketExist(String bucketName) throws AmazonClientException {
        return delegate.doesBucketExist(bucketName);
    }

    @Override
    public boolean doesBucketExistV2(String s) throws SdkClientException {
        return delegate.doesBucketExistV2(s);
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest headBucketRequest) throws AmazonClientException {
        return delegate.headBucket(headBucketRequest);
    }

    @Override
    public List<Bucket> listBuckets() throws AmazonClientException {
        return delegate.listBuckets();
    }

    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest) throws AmazonClientException {
        return delegate.listBuckets(listBucketsRequest);
    }

    @Override
    public String getBucketLocation(String bucketName) throws AmazonClientException {
        return delegate.getBucketLocation(bucketName);
    }

    @Override
    public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws AmazonClientException {
        return delegate.getBucketLocation(getBucketLocationRequest);
    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws AmazonClientException {
        return delegate.createBucket(createBucketRequest);
    }

    @Override
    public Bucket createBucket(String bucketName) throws AmazonClientException {
        return delegate.createBucket(bucketName);
    }

    @Deprecated
    @Override
    public Bucket createBucket(String bucketName, Region region) throws AmazonClientException {
        return delegate.createBucket(bucketName, region);
    }

    @Deprecated
    @Override
    public Bucket createBucket(String bucketName, String region) throws AmazonClientException {
        return delegate.createBucket(bucketName, region);
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key) throws AmazonClientException {
        return delegate.getObjectAcl(bucketName, key);
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key, String versionId) throws AmazonClientException {
        return delegate.getObjectAcl(bucketName, key, versionId);
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclRequest getObjectAclRequest) throws AmazonClientException {
        return delegate.getObjectAcl(getObjectAclRequest);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, AccessControlList acl) throws AmazonClientException {
        delegate.setObjectAcl(bucketName, key, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList acl) throws AmazonClientException {
        delegate.setObjectAcl(bucketName, key, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, AccessControlList acl)
            throws AmazonClientException {
        delegate.setObjectAcl(bucketName, key, versionId, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, CannedAccessControlList acl)
            throws AmazonClientException {
        delegate.setObjectAcl(bucketName, key, versionId, acl);
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws AmazonClientException {
        delegate.setObjectAcl(setObjectAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(String bucketName) throws AmazonClientException {
        return delegate.getBucketAcl(bucketName);
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws AmazonClientException {
        delegate.setBucketAcl(setBucketAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws AmazonClientException {
        return delegate.getBucketAcl(getBucketAclRequest);
    }

    @Override
    public void setBucketAcl(String bucketName, AccessControlList acl) throws AmazonClientException {
        delegate.setBucketAcl(bucketName, acl);
    }

    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList acl) throws AmazonClientException {
        delegate.setBucketAcl(bucketName, acl);
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws AmazonClientException {
        return delegate.getObjectMetadata(bucketName, key);
    }

    @Override
    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest) throws AmazonClientException {
        return delegate.getObjectMetadata(getObjectMetadataRequest);
    }

    @Override
    public S3Object getObject(String bucketName, String key) throws AmazonClientException {
        return delegate.getObject(bucketName, key);
    }

    @Override
    public S3Object getObject(GetObjectRequest getObjectRequest) throws AmazonClientException {
        return delegate.getObject(getObjectRequest);
    }

    @Override
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File destinationFile) throws AmazonClientException {
        return delegate.getObject(getObjectRequest, destinationFile);
    }

    @Override
    public String getObjectAsString(String bucketName, String key) throws AmazonClientException {
        return delegate.getObjectAsString(bucketName, key);
    }

    @Override
    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        return delegate.getObjectTagging(getObjectTaggingRequest);
    }

    @Override
    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) {
        return delegate.setObjectTagging(setObjectTaggingRequest);
    }

    @Override
    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        return delegate.deleteObjectTagging(deleteObjectTaggingRequest);
    }

    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest) throws AmazonClientException {
        delegate.deleteBucket(deleteBucketRequest);
    }

    @Override
    public void deleteBucket(String bucketName) throws AmazonClientException {
        delegate.deleteBucket(bucketName);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws AmazonClientException {
        return delegate.putObject(putObjectRequest);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file) throws AmazonClientException {
        return delegate.putObject(bucketName, key, file);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws AmazonClientException {
        return delegate.putObject(bucketName, key, input, metadata);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, String content) throws AmazonClientException {
        return delegate.putObject(bucketName, key, content);
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
            String destinationKey) throws AmazonClientException {
        return delegate.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws AmazonClientException {
        return delegate.copyObject(copyObjectRequest);
    }

    @Override
    public CopyPartResult copyPart(CopyPartRequest copyPartRequest) throws AmazonClientException {
        return delegate.copyPart(copyPartRequest);
    }

    @Override
    public void deleteObject(String bucketName, String key) throws AmazonClientException {
        delegate.deleteObject(bucketName, key);
    }

    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest) throws AmazonClientException {
        delegate.deleteObject(deleteObjectRequest);
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws AmazonClientException {
        return delegate.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public void deleteVersion(String bucketName, String key, String versionId) throws AmazonClientException {
        delegate.deleteVersion(bucketName, key, versionId);
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws AmazonClientException {
        delegate.deleteVersion(deleteVersionRequest);
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(String bucketName) throws AmazonClientException {
        return delegate.getBucketLoggingConfiguration(bucketName);
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(
            GetBucketLoggingConfigurationRequest getBucketLoggingConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketLoggingConfiguration(getBucketLoggingConfigurationRequest);
    }

    @Override
    public void setBucketLoggingConfiguration(SetBucketLoggingConfigurationRequest setBucketLoggingConfigurationRequest)
            throws AmazonClientException {
        delegate.setBucketLoggingConfiguration(setBucketLoggingConfigurationRequest);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName) throws AmazonClientException {
        return delegate.getBucketVersioningConfiguration(bucketName);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(
            GetBucketVersioningConfigurationRequest getBucketVersioningConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketVersioningConfiguration(getBucketVersioningConfigurationRequest);
    }

    @Override
    public void setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest)
            throws AmazonClientException {
        delegate.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) {
        return delegate.getBucketLifecycleConfiguration(bucketName);
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(
            GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        return delegate.getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest);
    }

    @Override
    public void setBucketLifecycleConfiguration(String bucketName, BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        delegate.setBucketLifecycleConfiguration(bucketName, bucketLifecycleConfiguration);
    }

    @Override
    public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest) {
        delegate.setBucketLifecycleConfiguration(setBucketLifecycleConfigurationRequest);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(String bucketName) {
        delegate.deleteBucketLifecycleConfiguration(bucketName);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(
            DeleteBucketLifecycleConfigurationRequest deleteBucketLifecycleConfigurationRequest) {
        delegate.deleteBucketLifecycleConfiguration(deleteBucketLifecycleConfigurationRequest);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(String bucketName) {
        return delegate.getBucketCrossOriginConfiguration(bucketName);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(
            GetBucketCrossOriginConfigurationRequest getBucketCrossOriginConfigurationRequest) {
        return delegate.getBucketCrossOriginConfiguration(getBucketCrossOriginConfigurationRequest);
    }

    @Override
    public void setBucketCrossOriginConfiguration(String bucketName,
            BucketCrossOriginConfiguration bucketCrossOriginConfiguration) {
        delegate.setBucketCrossOriginConfiguration(bucketName, bucketCrossOriginConfiguration);
    }

    @Override
    public void setBucketCrossOriginConfiguration(
            SetBucketCrossOriginConfigurationRequest setBucketCrossOriginConfigurationRequest) {
        delegate.setBucketCrossOriginConfiguration(setBucketCrossOriginConfigurationRequest);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(String bucketName) {
        delegate.deleteBucketCrossOriginConfiguration(bucketName);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(
            DeleteBucketCrossOriginConfigurationRequest deleteBucketCrossOriginConfigurationRequest) {
        delegate.deleteBucketCrossOriginConfiguration(deleteBucketCrossOriginConfigurationRequest);
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(String bucketName) {
        return delegate.getBucketTaggingConfiguration(bucketName);
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(
            GetBucketTaggingConfigurationRequest getBucketTaggingConfigurationRequest) {
        return delegate.getBucketTaggingConfiguration(getBucketTaggingConfigurationRequest);
    }

    @Override
    public void setBucketTaggingConfiguration(String bucketName, BucketTaggingConfiguration bucketTaggingConfiguration) {
        delegate.setBucketTaggingConfiguration(bucketName, bucketTaggingConfiguration);
    }

    @Override
    public void setBucketTaggingConfiguration(SetBucketTaggingConfigurationRequest setBucketTaggingConfigurationRequest) {
        delegate.setBucketTaggingConfiguration(setBucketTaggingConfigurationRequest);
    }

    @Override
    public void deleteBucketTaggingConfiguration(String bucketName) {
        delegate.deleteBucketTaggingConfiguration(bucketName);
    }

    @Override
    public void deleteBucketTaggingConfiguration(
            DeleteBucketTaggingConfigurationRequest deleteBucketTaggingConfigurationRequest) {
        delegate.deleteBucketTaggingConfiguration(deleteBucketTaggingConfigurationRequest);
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(String bucketName) throws AmazonClientException {
        return delegate.getBucketNotificationConfiguration(bucketName);
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(
            GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketNotificationConfiguration(getBucketNotificationConfigurationRequest);
    }

    @Override
    public void setBucketNotificationConfiguration(
            SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest) throws AmazonClientException {
        delegate.setBucketNotificationConfiguration(setBucketNotificationConfigurationRequest);
    }

    @Override
    public void setBucketNotificationConfiguration(String bucketName,
            BucketNotificationConfiguration bucketNotificationConfiguration) throws AmazonClientException {
        delegate.setBucketNotificationConfiguration(bucketName, bucketNotificationConfiguration);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName) throws AmazonClientException {
        return delegate.getBucketWebsiteConfiguration(bucketName);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(
            GetBucketWebsiteConfigurationRequest getBucketWebsiteConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketWebsiteConfiguration(getBucketWebsiteConfigurationRequest);
    }

    @Override
    public void setBucketWebsiteConfiguration(String bucketName, BucketWebsiteConfiguration configuration)
            throws AmazonClientException {
        delegate.setBucketWebsiteConfiguration(bucketName, configuration);
    }

    @Override
    public void setBucketWebsiteConfiguration(SetBucketWebsiteConfigurationRequest setBucketWebsiteConfigurationRequest)
            throws AmazonClientException {
        delegate.setBucketWebsiteConfiguration(setBucketWebsiteConfigurationRequest);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(String bucketName) throws AmazonClientException {
        delegate.deleteBucketWebsiteConfiguration(bucketName);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(DeleteBucketWebsiteConfigurationRequest deleteBucketWebsiteConfigurationRequest)
            throws AmazonClientException {
        delegate.deleteBucketWebsiteConfiguration(deleteBucketWebsiteConfigurationRequest);
    }

    @Override
    public BucketPolicy getBucketPolicy(String bucketName) throws AmazonClientException {
        return delegate.getBucketPolicy(bucketName);
    }

    @Override
    public BucketPolicy getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws AmazonClientException {
        return delegate.getBucketPolicy(getBucketPolicyRequest);
    }

    @Override
    public void setBucketPolicy(String bucketName, String policyText) throws AmazonClientException {
        delegate.setBucketPolicy(bucketName, policyText);
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws AmazonClientException {
        delegate.setBucketPolicy(setBucketPolicyRequest);
    }

    @Override
    public void deleteBucketPolicy(String bucketName) throws AmazonClientException {
        delegate.deleteBucketPolicy(bucketName);
    }

    @Override
    public void deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws AmazonClientException {
        delegate.deleteBucketPolicy(deleteBucketPolicyRequest);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws AmazonClientException {
        return delegate.generatePresignedUrl(bucketName, key, expiration);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
            throws AmazonClientException {
        return delegate.generatePresignedUrl(bucketName, key, expiration, method);
    }

    @Override
    public URL generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) throws AmazonClientException {
        return delegate.generatePresignedUrl(generatePresignedUrlRequest);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
            throws AmazonClientException {
        return delegate.initiateMultipartUpload(request);
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws AmazonClientException {
        return delegate.uploadPart(request);
    }

    @Override
    public PartListing listParts(ListPartsRequest request) throws AmazonClientException {
        return delegate.listParts(request);
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest request) throws AmazonClientException {
        delegate.abortMultipartUpload(request);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
            throws AmazonClientException {
        return delegate.completeMultipartUpload(request);
    }

    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) throws AmazonClientException {
        return delegate.listMultipartUploads(request);
    }

    @Override
    public S3ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return delegate.getCachedResponseMetadata(request);
    }

    @Deprecated
    @Override
    public void restoreObject(RestoreObjectRequest request) throws AmazonServiceException {
        delegate.restoreObject(request);
    }

    @Override
    public RestoreObjectResult restoreObjectV2(RestoreObjectRequest restoreObjectRequest) throws AmazonServiceException {
        return delegate.restoreObjectV2(restoreObjectRequest);
    }

    @Deprecated
    @Override
    public void restoreObject(String bucketName, String key, int expirationInDays) throws AmazonServiceException {
        delegate.restoreObject(bucketName, key, expirationInDays);
    }

    @Override
    public void enableRequesterPays(String bucketName) throws AmazonClientException {
        delegate.enableRequesterPays(bucketName);
    }

    @Override
    public void disableRequesterPays(String bucketName) throws AmazonClientException {
        delegate.disableRequesterPays(bucketName);
    }

    @Override
    public boolean isRequesterPaysEnabled(String bucketName) throws AmazonClientException {
        return delegate.isRequesterPaysEnabled(bucketName);
    }

    @Override
    public void setBucketReplicationConfiguration(String bucketName, BucketReplicationConfiguration configuration)
            throws AmazonClientException {
        delegate.setBucketReplicationConfiguration(bucketName, configuration);
    }

    @Override
    public void setBucketReplicationConfiguration(
            SetBucketReplicationConfigurationRequest setBucketReplicationConfigurationRequest) throws AmazonClientException {
        delegate.setBucketReplicationConfiguration(setBucketReplicationConfigurationRequest);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(String bucketName) throws AmazonClientException {
        return delegate.getBucketReplicationConfiguration(bucketName);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(
            GetBucketReplicationConfigurationRequest getBucketReplicationConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketReplicationConfiguration(getBucketReplicationConfigurationRequest);
    }

    @Override
    public void deleteBucketReplicationConfiguration(String bucketName) throws AmazonClientException {
        delegate.deleteBucketReplicationConfiguration(bucketName);
    }

    @Override
    public void deleteBucketReplicationConfiguration(DeleteBucketReplicationConfigurationRequest request)
            throws AmazonClientException {
        delegate.deleteBucketReplicationConfiguration(request);
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectName) throws AmazonClientException {
        return delegate.doesObjectExist(bucketName, objectName);
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(String bucket) throws AmazonClientException {
        return delegate.getBucketAccelerateConfiguration(bucket);
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(
            GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws AmazonClientException {
        return delegate.getBucketAccelerateConfiguration(getBucketAccelerateConfigurationRequest);
    }

    @Override
    public void setBucketAccelerateConfiguration(String bucketName, BucketAccelerateConfiguration accelerateConfiguration)
            throws AmazonClientException {
        delegate.setBucketAccelerateConfiguration(bucketName, accelerateConfiguration);
    }

    @Override
    public void setBucketAccelerateConfiguration(SetBucketAccelerateConfigurationRequest setBucketAccelerateConfigurationRequest)
            throws AmazonClientException {
        delegate.setBucketAccelerateConfiguration(setBucketAccelerateConfigurationRequest);
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(String s, String s1)
            throws SdkClientException {
        return delegate.deleteBucketMetricsConfiguration(s, s1);
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(
            DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketMetricsConfiguration(deleteBucketMetricsConfigurationRequest);
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketMetricsConfiguration(s, s1);
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(
            GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.getBucketMetricsConfiguration(getBucketMetricsConfigurationRequest);
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(String s, MetricsConfiguration metricsConfiguration)
            throws SdkClientException {
        return delegate.setBucketMetricsConfiguration(s, metricsConfiguration);
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(
            SetBucketMetricsConfigurationRequest setBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.setBucketMetricsConfiguration(setBucketMetricsConfigurationRequest);
    }

    @Override
    public ListBucketMetricsConfigurationsResult listBucketMetricsConfigurations(
            ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketMetricsConfigurations(listBucketMetricsConfigurationsRequest);
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(String s, String s1)
            throws SdkClientException {
        return delegate.deleteBucketAnalyticsConfiguration(s, s1);
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(
            DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketAnalyticsConfiguration(deleteBucketAnalyticsConfigurationRequest);
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketAnalyticsConfiguration(s, s1);
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(
            GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.getBucketAnalyticsConfiguration(getBucketAnalyticsConfigurationRequest);
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(String s,
            AnalyticsConfiguration analyticsConfiguration) throws SdkClientException {
        return delegate.setBucketAnalyticsConfiguration(s, analyticsConfiguration);
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(
            SetBucketAnalyticsConfigurationRequest setBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.setBucketAnalyticsConfiguration(setBucketAnalyticsConfigurationRequest);
    }

    @Override
    public ListBucketAnalyticsConfigurationsResult listBucketAnalyticsConfigurations(
            ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketAnalyticsConfigurations(listBucketAnalyticsConfigurationsRequest);
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(String s, String s1)
            throws SdkClientException {
        return delegate.deleteBucketInventoryConfiguration(s, s1);
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(
            DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketInventoryConfiguration(deleteBucketInventoryConfigurationRequest);
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketInventoryConfiguration(s, s1);
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(
            GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.getBucketInventoryConfiguration(getBucketInventoryConfigurationRequest);
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(String s,
            InventoryConfiguration inventoryConfiguration) throws SdkClientException {
        return delegate.setBucketInventoryConfiguration(s, inventoryConfiguration);
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(
            SetBucketInventoryConfigurationRequest setBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.setBucketInventoryConfiguration(setBucketInventoryConfigurationRequest);
    }

    @Override
    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(
            ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketInventoryConfigurations(listBucketInventoryConfigurationsRequest);
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(String s) throws SdkClientException {
        return delegate.deleteBucketEncryption(s);
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest)
            throws SdkClientException {
        return delegate.deleteBucketEncryption(deleteBucketEncryptionRequest);
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(String s) throws SdkClientException {
        return delegate.getBucketEncryption(s);
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest)
            throws SdkClientException {
        return delegate.getBucketEncryption(getBucketEncryptionRequest);
    }

    @Override
    public SetBucketEncryptionResult setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest)
            throws SdkClientException {
        return delegate.setBucketEncryption(setBucketEncryptionRequest);
    }

    @Override
    public SelectObjectContentResult selectObjectContent(SelectObjectContentRequest selectObjectContentRequest)
            throws SdkClientException {
        return delegate.selectObjectContent(selectObjectContentRequest);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public Region getRegion() {
        return Region.EU_Ireland;
    }

    @Override
    public String getRegionName() {
        return delegate.getRegionName();
    }

    @Override
    public URL getUrl(String bucketName, String key) {
        return delegate.getUrl(bucketName, key);
    }

    @Override
    public AmazonS3Waiters waiters() {
        return delegate.waiters();
    }

    @Override
    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest setPublicAccessBlockRequest) {
        return delegate.setPublicAccessBlock(setPublicAccessBlockRequest);
    }

    @Override
    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        return delegate.getPublicAccessBlock(getPublicAccessBlockRequest);
    }

    @Override
    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        return delegate.deletePublicAccessBlock(deletePublicAccessBlockRequest);
    }

    @Override
    public GetBucketPolicyStatusResult getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        return delegate.getBucketPolicyStatus(getBucketPolicyStatusRequest);
    }

    @Override
    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest setObjectLegalHoldRequest) {
        return delegate.setObjectLegalHold(setObjectLegalHoldRequest);
    }

    @Override
    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        return delegate.getObjectLegalHold(getObjectLegalHoldRequest);
    }

    @Override
    public SetObjectLockConfigurationResult setObjectLockConfiguration(
            SetObjectLockConfigurationRequest setObjectLockConfigurationRequest) {
        return delegate.setObjectLockConfiguration(setObjectLockConfigurationRequest);
    }

    @Override
    public GetObjectLockConfigurationResult getObjectLockConfiguration(
            GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        return delegate.getObjectLockConfiguration(getObjectLockConfigurationRequest);
    }

    @Override
    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest setObjectRetentionRequest) {
        return delegate.setObjectRetention(setObjectRetentionRequest);
    }

    @Override
    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        return delegate.getObjectRetention(getObjectRetentionRequest);
    }

    @Override
    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest presignedUrlDownloadRequest) {
        return delegate.download(presignedUrlDownloadRequest);
    }

    @Override
    public void download(PresignedUrlDownloadRequest presignedUrlDownloadRequest, File file) {
        delegate.download(presignedUrlDownloadRequest, file);
    }

    @Override
    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest presignedUrlUploadRequest) {
        return delegate.upload(presignedUrlUploadRequest);
    }

}
