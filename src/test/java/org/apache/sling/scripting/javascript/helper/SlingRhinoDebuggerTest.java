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
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.SwingGui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SlingRhinoDebuggerTest {

    @Mock
    private static SlingContextFactory slingContextFactory;

    @Mock
    private static ContextFactory jsContextFactory;

    @Test
    void testSlingRhionDebugger() {
        SlingRhinoDebugger srd = new SlingRhinoDebugger("test");
        assertNotNull(srd, "SlingRhinoDebugger failed to initiated");

        // Can work with Sling Context Factory.
        srd.attachTo(slingContextFactory);
        verify(slingContextFactory, times(1)).addListener(Mockito.any());
        srd.detach();
        verify(slingContextFactory, times(1)).removeListener(Mockito.any());
        srd.attachTo(slingContextFactory);
        verify(slingContextFactory, times(2)).addListener(Mockito.any());
        assertTrue(triggerDebuggerMenuAction("Exit"), "Cannot find 'Exit' menu action in Rhino Debugger.");
        verify(slingContextFactory, timeout(500).times(1)).debuggerStopped();
        srd.detach();
        verify(slingContextFactory, times(2)).removeListener(Mockito.any());

        // Can function without relying on a Sling Context Factory.
        srd.attachTo(jsContextFactory);
        verify(jsContextFactory, times(1)).addListener(Mockito.any());
        srd.detach();
        verify(jsContextFactory, times(1)).removeListener(Mockito.any());
        assertTrue(triggerDebuggerMenuAction("Exit"), "Cannot find 'Exit' menu action in Rhino Debugger.");
        verify(slingContextFactory, timeout(500).times(1)).debuggerStopped();
    }

    // Discover and simulate a mouse click event on a GUI menu action item.
    private boolean triggerDebuggerMenuAction(String cmd) {
        SwingGui debugger = (SwingGui) (Arrays.asList(Frame.getFrames()).stream().filter(f -> f instanceof SwingGui)
                .findFirst().orElse(null));
        if (debugger != null) {
            List<JMenu> menus = Arrays.asList(debugger.getJMenuBar().getComponents()).stream()
                    .filter(m -> m instanceof JMenu).map(JMenu.class::cast).collect(Collectors.toList());
            for (JMenu menu : menus) {
                JMenuItem item = Arrays.asList(menu.getMenuComponents()).stream().filter(i -> i instanceof JMenuItem)
                        .map(JMenuItem.class::cast).filter(i -> i.getText().equals(cmd)).findFirst().orElse(null);
                if (item != null) {
                    item.doClick();
                    return true;
                }
            }
        }
        // cmd action menu itme not found
        return false;
    }
}