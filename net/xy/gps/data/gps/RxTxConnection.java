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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import net.xy.codebasel.Log;
import net.xy.codebasel.Utils;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;

/**
 * connector for serial and com ports
 * 
 * @author Xyan
 * 
 */
public class RxTxConnection {
  /**
   * messages
   */
  private static final Config TEXT_PORT_INUSE = Cfg.register("gps.rxtx.error.port.inuser", "Port already in use");
  private static final Config TEXT_ERROR_IO = Cfg.register("gps.rxtx.error.io", "Error on read/write");
  private static final Config TEXT_CONNECT = Cfg.register("gps.rxtx.state.connecting", "Try to etablish an connection");
  /**
   * all known ports
   */
  public static final Set ports = new HashSet();

  public static boolean init() {
    final Enumeration foundPorts = CommPortIdentifier.getPortIdentifiers();
    while (foundPorts.hasMoreElements()) {
      final CommPortIdentifier port = (CommPortIdentifier) foundPorts.nextElement();
      switch (port.getPortType()) {
      case CommPortIdentifier.PORT_SERIAL:
        ports.add(port);
        break;
      }
    }
    return true;
  }

  public static void connect(final CommPortIdentifier port, final IConnectionHandler handler) {
    if (port.isCurrentlyOwned()) {
      Log.error(TEXT_PORT_INUSE);
      return;
    }
    try {
      final CommPort comm = port.open("XY.Gomps", 2000);
      if (comm instanceof SerialPort) {
        final SerialPort com = (SerialPort) comm;
        Log.notice(TEXT_CONNECT, new Object[] { com.getName() });
        try {
          com.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
          final InputStream in = com.getInputStream();
          final OutputStream out = com.getOutputStream();
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
        } catch (final IOException e) {
          Log.error(TEXT_ERROR_IO, new Object[] { e.getMessage() });
        } catch (final UnsupportedCommOperationException e) {
          Log.error(TEXT_ERROR_IO, new Object[] { e.getMessage() });
        } finally {
          com.close();
        }
      }
    } catch (final PortInUseException e) {
      Log.error(TEXT_PORT_INUSE, new Object[] { e.getMessage() });
    }
  }
}