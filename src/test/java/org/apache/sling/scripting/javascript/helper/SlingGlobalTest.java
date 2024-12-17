/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.scripting.javascript.helper;

import java.awt.Frame;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.SwingGui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SlingGlobalTest {

    private static final List<String> functionNames = Arrays.asList(new String[]{"load", "print", "require"});
    @Test
    void testSlingGlobalInitUnseal() {
        ImporterTopLevel scope = new ImporterTopLevel();
        SlingGlobal.init(scope, false);
        for( String func: functionNames) {
            IdFunctionObject ifo = (IdFunctionObject)scope.get(func);
            assertNotNull(ifo, func + "() is undefined.");
            assertTrue(!ifo.isSealed(), func + "() is undefined.");
        }
    }

    @Test
    void testSlingGlobalInitSealed() {
        ImporterTopLevel scope = new ImporterTopLevel();
        SlingGlobal.init(scope, true);
        for( String func: functionNames) {
            IdFunctionObject ifo = (IdFunctionObject)scope.get(func);
            assertNotNull(ifo, func + "() is undefined.");
            assertTrue(ifo.isSealed(), func + "() is undefined.");
        }
    }

}