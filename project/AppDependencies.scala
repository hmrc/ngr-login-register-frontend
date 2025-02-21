import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.7.0"
  private val hmrcMongoVersion = "2.4.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"                        % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"                        % "11.11.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                                % hmrcMongoVersion,
    "com.beachape"            %% "enumeratum-play-json"                              % "1.8.2",
    "uk.gov.hmrc"             %% "domain-play-30"                                    % "10.0.0",
    "uk.gov.hmrc"             %% "centralised-authorisation-resource-client-play-30" % "1.2.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % hmrcMongoVersion            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.18.3"                    % Test,
  )

  val it = Seq(
    "com.github.tomakehurst" % "wiremock" % "3.0.0-beta-7" % "test",
  )
}
