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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import net.xy.codebasel.Log;
import net.xy.codebasel.ObjectArray;
import net.xy.codebasel.config.Cfg;
import net.xy.codebasel.config.Cfg.Config;

public class Discoverer implements DiscoveryListener {
  /**
   * messages
   */
  private static final Config TEXT_FOUND_DEVICE = Cfg.register("gps.jsr82.found.device", "An device was found");
  private static final Config TEXT_FOUND_SERVICE = Cfg.register("gps.jsr82.found.service", "An service was discovered");
  private static final Config TEXT_SEARCH_SERVICE_ERROR = Cfg.register("gps.jsr82.search.error.service",
      "Error on service discovery");
  private static final Config TEXT_INQUIRY_COMPLETE_ERROR = Cfg.register("gps.jsr82.search.error",
      "Error on service/device discovery");
  /**
   * contains all discovered deives as RemoteDevice => DeviceClass
   */
  public final Map devices = Collections.synchronizedMap(new HashMap());
  /**
   * contains all found services as ServiceRecord
   */
  public final ObjectArray services = new ObjectArray();
  /**
   * flag to indicate search has finished
   */
  private boolean isComplete = false;

  public void deviceDiscovered(final RemoteDevice btDevice, final DeviceClass cod) {
    if (isComplete) {
      isComplete = false;
    }
    try {
      Log.comment(TEXT_FOUND_DEVICE, new Object[] { btDevice.getFriendlyName(true) });
      devices.put(btDevice, cod);
    } catch (final IOException e) {
      throw new RuntimeException("Exception on gettint device name", e);
    }
  }

  public void servicesDiscovered(final int transID, final ServiceRecord[] servRecord) {
    if (isComplete) {
      isComplete = false;
    }
    Log.comment(TEXT_FOUND_SERVICE);
    services.add(servRecord);
  }

  public void serviceSearchCompleted(final int transID, final int respCode) {
    if (isComplete) {
      isComplete = false;
    }
    switch (respCode) {
    case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
    case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
    case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
    case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
      break;
    case DiscoveryListener.SERVICE_SEARCH_ERROR:
    default:
      Log.notice(TEXT_SEARCH_SERVICE_ERROR, new Object[] { Integer.valueOf(transID) });
      break;
    }
  }

  public void inquiryCompleted(final int discType) {
    isComplete = true;
    switch (discType) {
    case DiscoveryListener.INQUIRY_COMPLETED:
    case DiscoveryListener.INQUIRY_TERMINATED:
      break;
    case DiscoveryListener.INQUIRY_ERROR:
    default:
      Log.notice(TEXT_INQUIRY_COMPLETE_ERROR);
      break;
    }
  }

  /**
   * is the inquiry complete
   * 
   * @return
   */
  public boolean isComplete() {
    return isComplete;
  }
}