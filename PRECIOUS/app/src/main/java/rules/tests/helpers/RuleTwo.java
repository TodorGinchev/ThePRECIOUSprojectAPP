package rules.tests.helpers;

/**
 * Created by christopher on 10.08.16.
 */

public class RuleTwo {
    public static final String r = "{\n" +
            "  \"_id\": \"56f3cf298ab1a2ec0ec388f2\",\n" +
            "  \"name\": \"This is a rule testing the table lookup\",\n" +
            "  \"description\": \"This is a rule description\",\n" +
            "  \"triggers\": [\n" +
            "    {\n" +
            "      \"key\": \"$time_12\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"$time_00\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"actions\": [\n" +
            "    {\n" +
            "      \"key\": \"$open_app_stream\",\n" +
            "      \"priority\": 0," +
            "      \"parameters\": [\n" +
            "        {\n" +
            "          \"definedBy\": \"user\",\n"+
            "          \"value\": \"value\",\n" +
            "          \"key\": \"param\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"operation\": {\n" +
            "    \"type\": \"$any\",\n" +
            "    \"conditions\": [\n" +
            "      {\n" +
            "        \"value\": 0,\n" +
            "        \"type\": \"$reference\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"conditions\": [\n" +
            "    {\n" +
            "      \"comparator\": \"$eq\",\n" +
            "      \"name\": \"lookup1\",\n" +
            "      \"parameters\": [\n" +
            "        {\n" +
            "          \"definedBy\": \"lookup\",\n" +
            "          \"lookupValue\": {\n" +
            "              \"type\" : \"table\",\n" +
            "              \"parameter\" : {\n" +
            "                  \"tableId\" : \"table1\",\n" +
            "                  \"row\" : {\n" +
            "                      \"definedBy\": \"user\",\n" +
            "                      \"userDefinedValue\": \"1\"\n" +
            "                  },\n" +
            "                  \"column\" : {\n" +
            "                      \"definedBy\" : \"data\",\n" +
            "                      \"key\": \"$user_id\",\n" +
            "                      \"arrayReduce\":\"$first\",\n" +
            "                  }\n" +
            "              }\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"definedBy\": \"user\",\n" +
            "          \"userDefinedValue\": \"1:1\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"priority\": 0,\n" +
            "  \"created\": 1458818857790\n" +
            "}";
}
