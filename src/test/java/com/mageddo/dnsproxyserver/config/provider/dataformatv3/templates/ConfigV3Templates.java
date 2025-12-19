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
            protocol: UDP_TCP
            port: 53
            noEntriesResponseCode: 3
          web:
            port: 5380
        solver:
          remote:
            active: true
            dnsServers:
            - 8.8.8.8
            - 4.4.4.4:53
            circuitBreaker:
                failureThreshold: null
                failureThresholdCapacity: null
                successThreshold: null
                testDelay: null
                type: STATIC_THRESHOLD
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
                target:
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

  public static String defaultJson_2025_12() {
    return """
        {
          "defaultDns" : {
            "active" : true,
            "resolvConf" : {
              "overrideNameServers" : true,
              "paths" : "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf"
            }
          },
          "log" : {
            "file" : "console",
            "level" : "DEBUG"
          },
          "server" : {
            "dns" : {
              "protocol" : "UDP_TCP",
              "noEntriesResponseCode" : 3,
              "port" : 53
            },
            "web" : {
              "port" : 5380
            }
          },
          "solver" : {
            "docker" : {
              "dockerDaemonUri" : null,
              "domain" : "docker",
              "dpsNetwork" : {
                "autoConnect" : false,
                "autoCreate" : false,
                "name" : "dps"
              },
              "hostMachineFallback" : true,
              "registerContainerNames" : false
            },
            "local" : {
              "activeEnv" : "",
              "envs" : [ {
                "hostnames" : [ {
                  "hostname" : "github.com",
                  "ip" : "192.168.0.1",
                  "target" : null,
                  "ttl" : 255,
                  "type" : "A"
                } ],
                "name" : ""
              } ]
            },
            "remote" : {
              "active" : true,
              "circuitBreaker" : {
                "failureThreshold" : null,
                "failureThresholdCapacity" : null,
                "successThreshold" : null,
                "testDelay" : null,
                "type" : "STATIC_THRESHOLD"
              },
              "dnsServers" : [ "8.8.8.8", "4.4.4.4:53" ]
            },
            "stub" : {
              "domainName" : "stub"
            },
            "system" : {
              "hostMachineHostname" : "host.docker"
            }
          },
          "version" : 3
        }
        """;
  }
  public static String buildJson() {
    return JsonUtils.prettify("""
        {
          "version": 3,
          "server": {
            "dns": {
              "protocol": "UDP_TCP",
              "port": 53,
              "noEntriesResponseCode": 3
            },
            "web": {
              "port": 5380
            }
          },
          "solver": {
            "remote": {
              "active": true,
              "dnsServers": [
                "8.8.8.8", "4.4.4.4:53"
              ],
              "circuitBreaker": {
                "failureThreshold" : null,
                "failureThresholdCapacity" : null,
                "successThreshold" : null,
                "testDelay" : null,
                "type": "STATIC_THRESHOLD"
              }
            },
            "docker": {
              "registerContainerNames": false,
              "domain": "docker",
              "hostMachineFallback": true,
              "dpsNetwork": {
                "name": "dps",
                "autoCreate": false,
                "autoConnect": false,
                "configs" : null
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
                      "target": null,
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
