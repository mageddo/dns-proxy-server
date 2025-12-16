package com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates;

import com.mageddo.dataformat.yaml.YamlUtils;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3JsonMapper;
import com.mageddo.json.JsonUtils;

public class ConfigV3Templates {

  public static String buildYaml() {
    return YamlUtils.format("""
      ---
      version: 3
      server:
        dns:
          port: 53
          noEntriesResponseCode: 3
        web:
          port: 5380
        protocol: UDP_TCP
      solver:
        remote:
          active: true
          dnsServers:
          - 8.8.8.8
          - 4.4.4.4:53
          circuitBreaker:
            name: STATIC_THRESHOLD
        docker:
          registerContainerNames: false
          domain: docker
          hostMachineFallback: true
          dpsNetwork:
            name: dps
            autoCreate: false
            autoConnect: false
          dockerDaemonUri:\s
        system:
          hostMachineHostname: host.docker
        local:
          activeEnv: ''
          envs:
          - name: ''
            hostnames:
            - type: A
              hostname: github.com
              ip: 192.168.0.1
              ttl: 255
        stub:
          domainName: stub
      defaultDns:
        active: true
        resolvConf:
          paths: "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf"
          overrideNameServers: true
      log:
        level: DEBUG
        file: console
      """);
  }

  public static ConfigV3 build() {
    return ConfigV3JsonMapper.of(buildJson());
  }

  public static String buildJson() {
    return JsonUtils.prettify("""
      {
        "version": 3,
        "server": {
          "dns": {
            "port": 53,
            "noEntriesResponseCode": 3
          },
          "web": {
            "port": 5380
          },
          "protocol": "UDP_TCP"
        },
        "solver": {
          "remote": {
            "active": true,
            "dnsServers": [
              "8.8.8.8", "4.4.4.4:53"
            ],
            "circuitBreaker": {
              "name": "STATIC_THRESHOLD"
            }
          },
          "docker": {
            "registerContainerNames": false,
            "domain": "docker",
            "hostMachineFallback": true,
            "dpsNetwork": {
              "name": "dps",
              "autoCreate": false,
              "autoConnect": false
            },
            "dockerDaemonUri": null
          },
          "system": {
            "hostMachineHostname": "host.docker"
          },
          "local": {
            "activeEnv": "",
            "envs": [
              {
                "name": "",
                "hostnames": [
                  {
                    "type": "A",
                    "hostname": "github.com",
                    "ip": "192.168.0.1",
                    "ttl": 255
                  }
                ]
              }
            ]
          },
          "stub": {
            "domainName": "stub"
          }
        },
        "defaultDns": {
          "active": true,
          "resolvConf": {
            "paths": "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf",
            "overrideNameServers": true
          }
        },
        "log": {
          "level": "DEBUG",
          "file": "console"
        }
      }
      """);
  }

}
