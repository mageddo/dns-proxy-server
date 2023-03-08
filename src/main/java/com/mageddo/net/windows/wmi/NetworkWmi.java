package com.mageddo.net.windows.wmi;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;
import com.mageddo.commons.lang.CloseQueue;
import com.mageddo.commons.lang.ExecutionException;
import com.mageddo.net.windows.registry.NetworkRegistry;
import com.mageddo.wmi.ComUtils;
import com.mageddo.wmi.SafeArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * https://community.spiceworks.com/topic/405339-replace-static-dns-settings-with-wmi-and-powershell
 * https://learn.microsoft.com/en-us/windows/win32/cimwin32prov/win32-networkadapterconfiguration
 * https://learn.microsoft.com/en-us/archive/msdn-magazine/2017/march/introducing-the-safearray-data-structure
 * https://stackoverflow.com/questions/55651597/how-to-i-access-the-data-of-a-wmi-query-via-jna-safearray-result
 * https://stackoverflow.com/questions/26745617/win32com-client-dispatch-cherrypy-coinitialize-has-not-been-called
 * https://learn.microsoft.com/en-us/windows/win32/wmisdk/describing-a-class-object-path
 * https://github.com/tpn/winsdk-10/blob/9b69fd26ac0c7d0b83d378dba01080e93349c2ed/Include/10.0.10240.0/um/WbemCli.h#L1959
 * https://github.com/tpn/winsdk-10/blob/9b69fd26ac0c7d0b83d378dba01080e93349c2ed/Include/10.0.10240.0/um/WbemCli.h#L2123
 * 25 ExecMethod
 */
public class NetworkWmi implements AutoCloseable {

  private final ActiveXComponent connection;
  private final CloseQueue closeables;

  public NetworkWmi() {
    this.closeables = new CloseQueue();
    this.connection = this.connect();
  }

  public int updateInterfaceDns(String dnsServer) {
    try {
      this.forEachInterface(item -> {
        ComUtils.checkRC(Dispatch.call(item, "SetDNSServerSearchOrder", SafeArrayUtils.fromStringArray(dnsServer)));
        ComUtils.checkRC(Dispatch.call(item, "SetDynamicDNSRegistration", new Variant(false)));
      });
    } catch (ExecutionException e) {
      return e.getCode();
    }
    return 0;
  }

  public int activateDynamicRegistration() {
    try {
      this.forEachInterface(item -> {
        ComUtils.checkRC(Dispatch.call(item, "SetDynamicDNSRegistration", new Variant(true)));
      });
    } catch (ExecutionException e) {
      return e.getCode();
    }
    return 0;
  }

  /**
   * See https://learn.microsoft.com/en-us/windows/win32/cimwin32prov/win32-networkadapterconfiguration
   * for all available fields and methods.
   *
   * @return all active interfaces.
   */
  public List<NetworkInterface> findInterfaces() {
    final var results = new ArrayList<NetworkInterface>();
    this.forEachInterface(item -> {
      final var netoworkId = Dispatch.call(item, "SettingID").toString();
//          .replaceAll("[\\{\\}]+", "");

//      final var automaticDns = WinRegistry.readString(
//          WinRegistry.HKEY_LOCAL_MACHINE,
//          String.format("\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\%s", netoworkId),
//
//      );
      results.add(NetworkInterface
          .builder()
          .id(netoworkId)
          .caption(Dispatch.call(item, "Caption").toString())
          .description(Dispatch.call(item, "Description").toString())
//          .dnsServers(List.of(Dispatch.call(item, "DNSServerSearchOrder").toString()))
          .dnsServers(List.of(Dispatch.call(item, "DNSServerSearchOrder").toString()))
          .staticDnsServers(NetworkRegistry.findStaticDnsServers(netoworkId))
          .build()
      );
    });
    return results;
  }

  public void forEachInterface(Consumer<Dispatch> c) {
    final var closeables = new CloseQueue();
    try {
      final var enumVar = this.findInterfacesVariantEnum();
      closeables.add(enumVar::safeRelease);
      while (enumVar.hasMoreElements()) {
        final var item = enumVar.nextElement().toDispatch();
        closeables.add(item::safeRelease);
        c.accept(item);
      }
    } finally {
      closeables.close();
    }
  }

  public EnumVariant findInterfacesVariantEnum() {
    final var query = "SELECT * FROM Win32_NetworkAdapterConfiguration WHERE (IPEnabled=TRUE)";
    final var collection = this.connection.invoke("ExecQuery", new Variant(query));
    this.closeables.add(collection::safeRelease);

    final var enumVariant = new EnumVariant(collection.toDispatch());
    this.closeables.add(enumVariant::safeRelease);
    return enumVariant;
  }

  ActiveXComponent connect() {
    final var wmi = new ActiveXComponent("WbemScripting.SWbemLocator");
    this.closeables.add(wmi::safeRelease);

    final var conn = wmi.invoke("ConnectServer");
    this.closeables.add(conn::safeRelease);

    final var connection = new ActiveXComponent(conn.toDispatch());
    this.closeables.add(connection::safeRelease);
    return connection;
  }

  @Override
  public void close() throws Exception {
    this.closeables.close();
  }

}
