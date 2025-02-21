
ngr-login-register-frontend
================

## Nomenclature


## Technical documentation


### Before running the app (if applicable)

Ensure that you have the latest versions of the required services and that they are running. This can be done via service manager using the NGR_ALL profile.
```
sm2 --start NGR-ALL
sm2 --start NGR_LOGIN_REGISTER-FRONTEND -f
sm2 --stop  NGR_LOGIN_REGISTER-FRONTEND
```
### Run local changes:
* `cd` to the root of the project.
* `sbt run`
* `Note` the service will run on port 1502 by default
* `Setup your policies:`
* `1.` make sure `centralised-authorisation-policy-config` is running
* `2.` run the shell script `runMainPolicyJsonGenerator.sh` found in the `centralised-authorisation-policy-config` repo
* `3.` stop `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`
* `4.` start `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`

### Running the test suite
```
sbt clean compile coverage test
```
### Further documentation

shuttering:
* `QA` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/qa?teamName=Non+Domestic+Rates+Reform+Prog.
* `STAGING` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/staging?teamName=Non+Domestic+Rates+Reform+Prog.

## Licence
This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").