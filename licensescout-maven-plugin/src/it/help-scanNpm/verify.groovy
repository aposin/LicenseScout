/**
 * Copyright 2019 Association for the promotion of open-source insurance software and for the establishment of open interface standards in the insurance industry (Verein zur Förderung quelloffener Versicherungssoftware und Etablierung offener Schnittstellenstandards in der Versicherungsbranche)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

File buildLogFile = new File( basedir, "build.log" );

Map<String, Boolean> searchStrings = new HashMap<>();
searchStrings.put("licensescout:scanNpm", Boolean.FALSE);
searchStrings.put("writeResultsToDatabase (Default: false)", Boolean.FALSE);
searchStrings.put("User property: npmExcludedDirectoryNames", Boolean.FALSE);

buildLogFile.eachLine { line, count ->
    // println "line $count: $line";
    for(String searchString : searchStrings.keySet())
    {
        if (line.contains(searchString))
            {
                searchStrings.put(searchString, Boolean.TRUE);
            }
    }
}
for (Map.Entry entry : searchStrings.entrySet())
{
    assert entry.value : "line '" + entry.key + "' not found in build log"
}
