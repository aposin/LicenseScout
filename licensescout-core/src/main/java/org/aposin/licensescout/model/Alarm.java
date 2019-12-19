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
package org.aposin.licensescout.model;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.database.AlarmWriter;

/**
 * An alarm on an archive that should be stored in the database.
 * 
 * @see AlarmWriter
 * @see AlarmCause
 */
public class Alarm {

    private final AlarmCause alarmCause;
    private final Archive archive;

    /**
     * @param alarmCause
     * @param archive
     */
    public Alarm(AlarmCause alarmCause, Archive archive) {
        this.alarmCause = alarmCause;
        this.archive = archive;
    }

    /**
     * @return the alarmCause
     */
    public final AlarmCause getAlarmCause() {
        return alarmCause;
    }

    /**
     * @return the archive
     */
    public final Archive getArchive() {
        return archive;
    }

}
