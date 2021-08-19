<img src="https://talk.openmrs.org/uploads/default/original/2X/f/f1ec579b0398cb04c80a54c56da219b2440fe249.jpg" alt="OpenMRS"/>

# DHIS Connector Module

This module posts OpenMRS Period Indicator Report data to DHIS2 using the Reporting Rest module. Mappings between Period
Indicator Reports and DHIS2 Data Sets can be generated via the UI. The DHIS2 API is backed up for offline operation and
DXF files can be downloaded instead of posting to DHIS2 directly.
- This module so-far is only tested to support daily, weekly and monthly period types, For automated reporting, the module will only run once for each day, week or month only for the previous past period

## Setup

Before you use the DHIS Connector Module, you will need to configure *both* a Period Indicator Report in OpenMRS ([instructions](https://wiki.openmrs.org/pages/viewpage.action?pageId=19300405#BuildingReports(StepByStepGuide)-PeriodIndicatorReportStep-By-Step)) and the corresponding DHIS2 Data Set in DHIS2 ([instructions](https://docs.dhis2.org/en/use/user-guides/dhis-core-version-236/configuring-the-system/metadata.html#create_data_set)).

### Installation

First install the [Reporting Rest Module](https://modules.openmrs.org/#/show/121/reportingrest)<sup>†</sup> (you will also need the [Rest Module](https://addons.openmrs.org/show/org.openmrs.module.webservices-rest) and the [Reporting Module](https://addons.openmrs.org/show/org.openmrs.module.reporting)). Then download and install the [DHIS Connector Module](https://addons.openmrs.org/show/org.openmrs.module.dhismodule).

† You will actually need to download [this build](https://github.com/psbrandt/openmrs-module-reportingrest/releases/download/1.5.1/reportingrest-1.5.1.omod) of the Reporting Rest Module until [this change](https://github.com/psbrandt/openmrs-module-reportingrest/commit/270a44b45b88bf1ba60d60e90938475d1265f12e) is merged and released.

> Development installation Steps

Install depended on modules
* git clone https://github.com/openmrs/openmrs-module-reporting.git
  * Depends on some other modules
    * cd ../
      * git clone https://github.com/openmrs/openmrs-module-serialization.xstream.git
        * cd openmrs-module-serialization.xstream
        * git checkout tags/0.2.10
        * mvn clean install
        * cp omod/target/*.omod ~/.OpenMRS/modules
      * cd ../
      * git clone https://github.com/openmrs/openmrs-module-htmlwidgets.git
        * cd openmrs-module-htmlwidgets
        * git checkout tags/1.7.2
        * mvn clean install
        * cp omod/target/*.omod ~/.OpenMRS/modules
      * cd ../
      * git clone https://github.com/openmrs/openmrs-module-calculation.git
        * cd openmrs-module-calculation
        * git checkout tags/1.1
        * mvn clean install
        * cp omod/target/*.omod ~/.OpenMRS/modules
   * cd ../
   * cd openmrs-module-reporting
   * git checkout tags/0.9.4
   * mvn clean install
   * cp omod/target/*.omod ~/.OpenMRS/modules
* cd ../
* git clone https://github.com/openmrs/openmrs-module-reportingrest.git
  * Depends on rest webservices module as well as reporting above
    * cd ../
      * git clone https://github.com/openmrs/openmrs-module-webservices.rest.git
      * cd openmrs-module-webservices.rest
      * git checkout tags/2.5
      * mvn clean install
      * cp omod/target/*.omod ~/.OpenMRS/modules
  * cd ../
  * cd openmrs-module-reportingrest
  * git checkout tags/1.5
  * mvn clean install
  * cp omod/target/*.omod ~/.OpenMRS/modules

* Install DHIS connector module
  * cd ../
  * git clone http://github.com/jembi/openmrs-module-dhisconnector.git
  * mvn clean install
  * cp omod/target/*.omod ~/.OpenMRS/modules
* Start OpenMRS.


### Configuration

The first step is to configure the link to your DHIS2 server. This is done by clicking the *Configure DHIS Server* link under the *DHIS Connector Module* heading on the OpenMRS Administration page. You will need to know the URL of your target DHIS2 instance as well the details of a user that has access to the API. To test with the DHIS2 demo server, use the following details:

| Field | Value |
| ----- | ----- |
|URL | https://play.dhis2.org/demo|
|Username | admin|
|Password | district|

> :warning: **NB:** Since DHIS API pagination isn't handled yet, you will have to change the *Rest Max Results Absolute* Webservices Module global property to 2000. Do this by clicking the *Settings* link on the OpenMRS Administration page, then click *Webservices* on the bottom left. Change the value of the *Rest Max Results Absolute* property to 2000 and click save.
> Ensure to build your reports in OpenMRS but making sure each indicator id is uniquely named

## Mappings

Before you can send Period Indicator Report data to DHIS2, a mapping must exist between the report and a DHIS2 Data Set. Mappings can either be generated via the UI or placed in the correct location. Mappings are stored as JSON on the file system at `OPENMRS_DIR/dhisconnector/mappings/`. On Ubuntu, this usually corresponds to `/usr/share/tomcat7/.OpenMRS/dhisconnector/mappings/`.


### Create Mapping

To generate a mapping via the UI, click the *Create Mapping* link under the *DHIS Connector Module* heading on the OpenMRS Administration page. Then select the Period Indicator Report from the left menu and the corresponding DHIS2 Data Set from the right menu. Drag the Data Elements and Category Option Combos from the right to the matching row on the left as follows:

![](https://cloud.githubusercontent.com/assets/668093/12115457/35c47c4c-b3bb-11e5-8004-58f76fbdf0c1.gif)

Finally, click save and give your mapping a unique name.

### Exporting Existing Mapping

This module provides a way of exporting existing mappings which basically archives them into one download that can thereafter be imported/uploaded into the current or another OpenMRS instance.

### Uploading Existing Mapping

The module provides an option to import or upload mappings previously exported from the current or other instances, this feature is available and will be released as part of 0.1.2

## Location Mappings

Before you can send Period Indicator Report data to DHIS2, a mapping must exist between the OpenMRS Location and a DHIS2 Organization Unit. So when pushing data, the respective DHIS Organization Unit and OpenMRS Location of the Dataset will be automatically mapped. Location Mappings can be saved via the Location Mapping UI. Mappings are stored in the database at `dhisconnector_location_to_orgunit` table.

![DHIS Connector Module - Location Mapping](https://user-images.githubusercontent.com/27498587/129088257-e520345c-bf1e-4709-869b-b910000e492b.gif)

## Posting Data

To post data to the DHIS2 server or download the data in DXF format, click the *Run Reports* link under the *DHIS Connector Module* heading on the OpenMRS Administration page. Select the Period Indicator Report and the corresponding mapping to use.

Since Period Indicator Reports are always run for a specific location, you will also need to select the OpenMRS Location as well as the corresponding DHIS2 Organisation Unit. But with the automated location mapping feature, it will show the available location mappings when the mapping is selected. You can select the required location-orgunit mappings from the available mappings.

The date selector will changed based on the period type of the DHIS2 Data Set.

![DHIS Connector Module - Upgraded Run Reports UI](https://user-images.githubusercontent.com/27498587/129095601-9d9c3654-03f1-4854-a1b1-f8593201e753.gif)

Once you have selected a value for all the fields, click *Send Data* to post data directly to the DHIS2 server, or *Download JSON* to download the data in DXF format.

> To post backedup DHIS2 API, run; `bash postDHIS2API.sh` and enter required details

## Automation

To schedule/automate pushing data, click *Automation* link under the *DHIS Connector Module* heading on the OpenMRS Administration page.

To add mappings, choose the mapping from the dropdown lost and click `Add`.

You need to map the relevant OpenMRS Location and DHIS2 Organisation Unit using the Location Mapping UI before scheduling the mappings.

To push data manually, select the mappings and click `Run Selected`. Available OpenMRS locations and DHIS2 organization units will be mapped automatically. Automated reports will be ran and pushed automatically by the end of the relevant period. 

You can toggle the automation by checking/unchecking the tick box and clicking save.

![](https://user-images.githubusercontent.com/27498587/129963942-a0a9e482-f580-4d3c-8f96-7a6bb3d34bb6.gif)

## DHIS2 Backup

Every time a request is sent to the DHIS2 server, the resulting JSON is stored on the file system at `OPENMRS_DIR/dhisconnector/dhis2Backup/`. On Ubuntu, this usually corresponds to `/usr/share/tomcat7/.OpenMRS/dhisconnector/dhis2Backup/`. If the DHIS2 server is no longer reachable, these backed up API values will be used by the DHIS Connector Module.

For OpenMRS implementations that should operate offline, it is possible to pre-populate this dhis2Backup by using both the DHIS API Import and export pages. Assuming all the required resources have been backed up by the online implementation, the offline implementation should be able to function correctly without ever being able to reach the DHIS2 server.

## User Access Controlling

The DHIS Connector module comes with these user privileges.
1. `View Connection` - Viewing DHIS2 Connection
2. `Manage Connection` - Edit DHIS2 Connection
3. `View Location Mappings` - View location-orgunit mappings
4. `Manage Location Mappings` - Edit/Delete location-orgunit mappings
5. `View Automation` - View scheduled mappings
6. `Run Automation` - Run scheduled mappings
7. `Manage Automation` - Add/Edit/Delete scheduled mappings
8. `Run Reports` - Base privilege for running the reports
9. `Run Failed data` - Base privilege for pushing the failed data
10. `Manage Mappings` - Create/Edit Mappings
11. `Import and Export` - Import and export mappings and DHIS2 API

Users are required to have the necessary user privileges in order to use the functionalities of the module. The admins can create and assign roles with the combinations of privileges. If the required privilege is missing, the user will be redirected to the Home page.

In addition, users should have the privileges related to the used period indicator reports when pushing data. Also users must have `Manage Global Properties` to toggle automation and to update the DHIS2 Connection. [More Info...](https://talk.openmrs.org/t/dhis-connector-module-user-access-controlling/34059)


## Module Status

Jira Issues
  - [DHISConnector Module's issues](https://jembiprojects.jira.com/issues/?filter=13312)

Implemented
  - [x] Configure DHIS2 Server
  - [x] Drag and drop mapping generator
  - [x] Post data or download DXF
  - [x] Backup DHIS2 API for offline use
  - [x] Interface for uploading mappings
  - [x] Interface for exporting mappings
  - [x] Fix mapping creation UI to support multi-line DataElement names
  - [x] Interface for prepopulating DHIS2 API backup  
  - [x] Error handling
  - [x] Managing/editing mappings
  - [X] Scroll page when dragging mapping to top and bottom of page if necessary
  - [X] Generation, Downloading & Posting of ADX
  - [x] Automated reporting and posting to DHIS2 using configured mapping
  - [x] Support Quarterly, SixMonthly, SixMonthlyApril, FinancialApril, FinancialJuly, FinancialOct period types
  - [x] Add location mapping feature
  - [x] Implement role based user access controlling
  - [x] Support WeeklySunday, Biweekly, Bimonthly, FinancialNov, SixMonthlyNov, WeeklyWednesday, WeeklyThursday, WeeklySaturday period types in Automation

TODO
  - [ ] Support other types of OpenMRS reports
  - [ ] DHIS2 API pagination
  - [ ] Upgrade all dhis endpoints or api to work after 2.20
  - [ ] Support remaining period types in Run Reports


## License

[MPL 2.0 w/ HD](http://openmrs.org/license/) © [OpenMRS Inc.](http://www.openmrs.org/)
