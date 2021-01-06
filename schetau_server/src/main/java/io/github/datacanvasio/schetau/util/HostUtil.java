/*
 * Copyright 2020 DataCanvas
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

package io.github.datacanvasio.schetau.util;

import io.github.datacanvasio.schetau.service.dto.NodeDto;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class HostUtil {
    private HostUtil() {
    }

    @Nonnull
    public static NodeDto getMyNode() {
        NodeDto node = new NodeDto();
        node.setId(UUID.randomUUID().toString());
        InetAddress address = getAddress();
        if (address != null) {
            node.setHostAddress(address.getHostAddress());
            node.setHostName(address.getCanonicalHostName());
        }
        return node;
    }

    private static InetAddress getAddress() {
        InetAddress address = null;
        try {
            boolean found = false;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (!found && interfaces.hasMoreElements()) {
                NetworkInterface ifc = interfaces.nextElement();
                if (ifc.isUp()) {
                    Enumeration<InetAddress> addresses = ifc.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        address = addresses.nextElement();
                        if (address.isSiteLocalAddress()) {
                            found = true;
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return address;
    }
}
