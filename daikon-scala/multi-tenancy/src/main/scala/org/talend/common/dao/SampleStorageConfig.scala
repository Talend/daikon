package org.talend.common.dao

trait SampleStorageConfig

case object DBSampleStorageConfig extends SampleStorageConfig

trait S3SampleStorageConfig extends SampleStorageConfig

case class S3SampleStorageConfigEC2Auth(
  bucketName: String,
  path:       String
) extends S3SampleStorageConfig

case class S3SampleStorageConfigBasicAuth(
  region:     String,
  accessKey:  String,
  secret:     String,
  bucketName: String,
  path:       String,
  endpoint:   Option[String],
  signingReg: Option[String]
) extends S3SampleStorageConfig
