/**
 * This file is part of XY.Gomps, Copyright 2011 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 * XY.Gomps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * XY.Gomps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with XY.Gomps. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.gps.data.gps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.xy.codebasel.Log;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;

public class JSR82Connection {
  /**
   * message configuration
   */
  private static final Config TEXT_ADAPTER_OFF = Cfg
      .register("gps.jsr82.adapter.off", "The bluetooth adapter is turned off");
  private static final Config TEXT_NO_BLUETOOTH = Cfg.register("gps.jsr82.unavailable",
      "There could be no bluetooth adapter initialized");
  private static final Config TEXT_CANT_GETNAME = Cfg.register("gps.jsr82.cantDiscover",
      "Discovering device name not possible, checking unknown device for service");
  private static final Config TEXT_CANT_SELECT = Cfg.register("gps.jsr82.cantSelect",
      "Selecting services is actually not possible");
  private static final Config TEXT_CONNECTION_ERROR = Cfg.register("gps.jsr82.error.connect", "Error connecting to device");
  private static final Config TEXT_CONNECTION_UNSUPPORTED = Cfg.register("gps.jsr82.error.unsupported",
      "Unsupported connection protocol");
  private static final Config TEXT_CONNECTION_SVCCHECK = Cfg.register("gps.jsr82.query.service",
      "Checking device for service");
  private static final Config TEXT_ERROR_SVCCHECK = Cfg.register("gps.jsr82.error.query.service",
      "Checking device for service failed");

  /**
   * discoverer for this adapter
   */
  public static final Discoverer discoverer = new Discoverer();
  /**
   * all found devices
   */
  public static final Set devices = new HashSet();

  /**
   * internally inits the adapter
   * 
   * @return
   */
  public static boolean init() {
    LocalDevice adapter;
    try {
      adapter = LocalDevice.getLocalDevice();
    } catch (final BluetoothStateException e) {
      Log.error(TEXT_NO_BLUETOOTH);
      return false;
    }
    if (adapter != null) {
      if (!LocalDevice.isPowerOn()) {
        Log.warning(TEXT_ADAPTER_OFF);
      } else {
        final DiscoveryAgent agent = adapter.getDiscoveryAgent();
        if (agent != null) {
          while (true) {
            try {
              agent.startInquiry(DiscoveryAgent.GIAC, discoverer);
              break;
            } catch (final BluetoothStateException e) {
              Utils.sleep(40);
            }
          }
          // use known devices, RemoteDevice
          for (final Iterator i = discoverer.devices.keySet().iterator(); i.hasNext();) {
            devices.add(i.next());
          }
          RemoteDevice[] dev = agent.retrieveDevices(DiscoveryAgent.CACHED);
          if (dev != null) {
            for (int i = 0; i < dev.length; i++) {
              devices.add(dev[i]);
            }
          }
          dev = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);
          if (dev != null) {
            for (int i = 0; i < dev.length; i++) {
              devices.add(dev[i]);
            }
          }

          // found
          for (final Iterator i = devices.iterator(); i.hasNext();) {
            final RemoteDevice d = (RemoteDevice) i.next();
            try {
              Log.notice(TEXT_CONNECTION_SVCCHECK, new Object[] { d.getFriendlyName(false) });
            } catch (final IOException e) {
              Log.comment(TEXT_CANT_GETNAME, new Object[] { e.getMessage() });
            }
            try {
              agent.searchServices(null, new UUID[] { new UUID(0x0003) }, d, discoverer);
            } catch (final BluetoothStateException e) {
              Log.notice(TEXT_ERROR_SVCCHECK, new Object[] { e.getMessage() });
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * etablishes an connection between the handler an the given service
   * 
   * @param rec
   * @param handler
   */
  public static void connect(final ServiceRecord rec, final IConnectionHandler handler) {
    try {
      connect(rec.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false), handler);
    } catch (final BluetoothStateException e) {
      Log.error(TEXT_CANT_SELECT);
    } catch (final IOException e) {
      Log.error(TEXT_CONNECTION_ERROR);
    }
  }

  /**
   * internally connects the handler
   * 
   * @param svcStr
   * @param handler
   * @throws IOException
   */
  private static void connect(final String svcStr, final IConnectionHandler handler) throws IOException {
    final Connection con = Connector.open(svcStr, Connector.READ_WRITE, true);
    if (con instanceof StreamConnection) {
      final StreamConnection c = (StreamConnection) con;
      final InputStream in = c.openInputStream();
      final OutputStream out = c.openOutputStream();
      final byte[] buffer = new byte[1024];
      int inbuf = 0;
      while (!handler.close()) {
        if (in.available() > 0) {
          inbuf = in.read(buffer, 0, buffer.length);
          handler.in(buffer, inbuf);
        }
        if (handler.isAvailable()) {
          out.write(handler.getOutput());
        }
        Utils.sleep(40);
      }
      out.close();
      in.close();
    } else {
      Log.error(TEXT_CONNECTION_UNSUPPORTED, new Object[] { con });
      // com.intel.bluetooth.obex.OBEXClientSessionImpl for nokia obex
      throw new UnsupportedOperationException("Unsupported connection type");
    }
  }
}
