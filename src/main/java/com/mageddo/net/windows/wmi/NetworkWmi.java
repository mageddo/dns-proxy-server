package com.mageddo.net.windows.wmi;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;
import com.mageddo.commons.lang.CloseQueue;
import com.mageddo.wmi.ComUtils;
import com.mageddo.commons.lang.ExecutionException;
import com.mageddo.wmi.SafeArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

  public List<NetworkInterface> findInterfaces() {
    final var results = new ArrayList<NetworkInterface>();
    this.forEachInterface(item -> results.add(NetworkInterface
        .builder()
        .id(Dispatch.call(item, "SettingID").toString())
        .caption(Dispatch.call(item, "Caption").toString())
        .description(Dispatch.call(item, "Description").toString())
        .dnsServers(List.of(Dispatch.call(item, "DNSServerSearchOrder").toString()))
        .dynamicDnsRegistration(Dispatch.call(item, "DynamicDNSRegistration").toBoolean())
        .build()
    ));
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
