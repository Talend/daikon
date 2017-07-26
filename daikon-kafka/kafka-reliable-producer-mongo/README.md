Service mongo
==============

This module provides a mongo repo which allow storing "pending records" documents
This repo is an implementation for the module daikon-kafka

Pending record repository
------------------------------------
A pending record document represent a kafka message which could not be sent to a kafka broker, and which is waiting for been sent again
`PendingRecordRepository` implements basic operations on pending records

