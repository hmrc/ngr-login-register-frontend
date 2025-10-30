import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.3.0"
  private val hmrcMongoVersion = "2.10.0"
  private val enumeratumVersion = "1.9.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "12.19.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                                % hmrcMongoVersion,
    "com.beachape"            %% "enumeratum-play-json"                              % "1.9.0",
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "11.0.0",
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.14.0",
    "com.beachape"            %% "enumeratum-play"                                   %  enumeratumVersion
  )

  val test = Seq(
    "org.mockito"             %% "mockito-scala"              % "2.0.0"                     % Test,
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.21.2"                    % Test,
  )

  val it = Seq(
    "com.github.tomakehurst" % "wiremock" % "3.0.1" % "test",
  )
}
