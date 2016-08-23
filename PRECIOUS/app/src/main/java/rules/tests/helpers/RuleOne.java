package rules.tests.helpers;

/**
 * Created by christopher on 07.07.16.
 */

public class RuleOne {

    public static final String r =
            "{\n" +
                    "  \"_id\": \"56f3cf298ab1a2ec0ec388f2\",\n" +
                    "  \"name\": \"This is a rule\",\n" +
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
                    "      \"parameters\": [\n" +
                    "        {\n" +
                    "          \"definedBy\": \"user\",\n" +
                    "          \"value\": \"value\",\n" +
                    "          \"key\": \"param\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"operation\": {\n" +
                    "    \"type\": \"$and\",\n" +
                    "    \"conditions\": [\n" +
                    "      {\n" +
                    "        \"value\": 0,\n" +
                    "        \"type\": \"$reference\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"value\": 1,\n" +
                    "        \"type\": \"$reference\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  \"conditions\": [\n" +
                    "    {\n" +
                    "      \"comparator\": \"$eq\",\n" +
                    "      \"name\": \"c1c1\",\n" +
                    "      \"parameters\": [\n" +
                    "        {\n" +
                    "          \"definedBy\": \"user\",\n" +
                    "          \"userDefinedValue\": \"a\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"definedBy\": \"user\",\n" +
                    "          \"userDefinedValue\": \"a\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"comparator\": \"$lte\",\n" +
                    "      \"name\": \"muh\",\n" +
                    "      \"parameters\": [\n" +
                    "        {\n" +
                    "          \"definedBy\": \"data\",\n" +
                    "          \"key\": \"$user_steps\",\n" +
                    "          \"historicRequirements\": \"$yesterday\",\n" +
                    "          \"arrayReduce\": \"$sum\",\n" +
                    "          \"dataOption\": {\n" +
                    "            \"path\": \"steps\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"definedBy\": \"user\",\n" +
                    "          \"userDefinedValue\": 3000\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"created\": 1458818857790\n" +
                    "}";


}
