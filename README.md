# COMS-4156-Project
GitHub Repo for Team Project

## Postman Test Documentation
View the list of API calls made over the network using post man fully documented with the received result and parameters: https://documenter.getpostman.com/view/30499865/2s9YR85tUY

## Endpoints
This section describes the endpoints that our service provides, as well as their inputs and outputs.

#### POST /register-client
* This API registers a client application, and returns a network ID that the client
can use to access the service's other endpoints.
* Input: N/A
* Output: network-id (string)

#### POST /upload-doc
* This API uploads a document, which may be entirely new or a new version of an existing document.
If there are now more than three versions of the document, the oldest is deleted.
* Input: network-id (string), document-name (string), document-content (string / raw file contents)
* Output: N/A

#### GET /download-doc
* This API retrieves up to three versions of a specified document, in addition to document statistics and metadata.
* Input: network-id (string), document-name (string)
* Output: document-content-1 (string / raw file contents), document-content-2, document-content-3, document-statistics

#### POST /delete-doc
* This API deletes all versions of a specified document.
* Input: network-id (string), document-name (string)
* Output: N/A

#### GET /check-for-doc
* This API checks if a specified document exists, and, if it does, returns how many versions are stored.
* Input: network-id (string), document-name (string)
* Output: document-exists (boolean), version-count (integer)

## Tools used
This section includes notes on tools and technologies used in building this project, as well as any additional details if applicable.

* Firebase DB
* Maven Package Manager
* GitHub Actions CI
  * This is enabled via the "Actions" tab on GitHub.
  * Currently, this just runs a Maven build to make sure the code builds on branch 'main'.
* Checkstyle
  * We use Checkstyle for code reporting. Note that Checkstyle does NOT get run as part of the CI pipeline.
  * For running Checkstyle manually, you can use the "Checkstyle-IDEA" plugin for IntelliJ.
* SonarQube
  * TODO: Will do static analysis in second iteration.
* JUnit
  * JUnit tests get run automatically as part of the CI pipeline.
* Cobertura
  * TODO
* Postman
  * We used Postman for testing that the APIs work.
