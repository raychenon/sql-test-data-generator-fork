/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2021-2021 the original author or authors.
 */

package org.stdg.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Fast tests")
@SelectClasses( {  H2Test.class
                 , NotFullyManagedDatabaseTest.class
                 , DatasetRowApiTest.class
                 , SelectTest.class
                 , UpdateTest.class
                 , DeleteTest.class
                 , InsertTest.class
                 , H2DateTypesTest.class
                 , SortInsertStatementsTest.class
                 , SortInsertStatementsWithPkTest.class
                 , DatasetRowsMergingTest.class
                 , JdbcRoundtripTest.class} )
public class FastTestSuite {
}
