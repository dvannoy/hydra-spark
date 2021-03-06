/*
 * Copyright (C) 2017 Pluralsight, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implie
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hydra.spark.api

import com.typesafe.config.Config


trait JobDetails {
  def name: String
}

/**
  * Contains all the information needed to run a dispatch, but without
  * a context associated with it.
  *
  * @param name        The unique name for this dispatch job.
  * @param source      The materialized source
  * @param operations  The list of operations
  * @param isStreaming Whether or not this is a streaming dispatch
  * @param dsl         The underlying DSL converted to a resolved Typesafe config format
  *
  *                    Created by alexsilva on 1/3/17.
  */
case class TransformationDetails[S](name: String, source: Source[S], operations: Seq[DFOperation], isStreaming: Boolean,
                                    dsl: Config) extends JobDetails

case class ReplicationDetails(name: String, topics: Either[List[String], String],
                              startingOffsets: String,
                              primaryKeys: Map[String, String],
                              saveMode: String,
                              properties: Config,
                              connectionInfo: Map[String, String]) extends JobDetails
