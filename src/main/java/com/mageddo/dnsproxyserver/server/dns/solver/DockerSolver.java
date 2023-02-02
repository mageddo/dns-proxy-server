package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.docker.DockerRepository;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerSolver implements Solver {

  private final DockerRepository dockerRepository;

  @Override
  public Message handle(Message reqMsg) {

    final var askedHost = Messages.findQuestionHostname(reqMsg);
    for (final var host : Wildcards.buildHostAndWildcards(askedHost)) {
      final var ip = this.dockerRepository.findHostIp(host);
      if (ip == null) {
        return null;
      }
      return Messages.aAnswer(reqMsg, ip);
    }


//    func (s DockerDnsSolver) doSolve(ctx context.Context, k string, q dns.Question) (*dns.Msg, error) {
//      logging.Debugf("solver=docker, status=solved-key, question=%s, hostname=%s, ip=%+v", ctx, q.Name, k, s.c.Get(k))
//      return s.getMsg(k, q), nil
//    }
//
//    func NewDockerSolver(c cache.Cache) DockerDnsSolver {
//      return DockerDnsSolver{c}
//    }
//
//    func (s DockerDnsSolver) getMsg(key string, question dns.Question) *dns.Msg {
//      ip := s.c.Get(key).(string)
//          ipArr := strings.Split(ip, ".")
//      i1, _ := strconv.Atoi(ipArr[0])
//      i2, _ := strconv.Atoi(ipArr[1])
//      i3, _ := strconv.Atoi(ipArr[2])
//      i4, _ := strconv.Atoi(ipArr[3])
//
//      rr := &dns.A{
//        Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 0},
//        A:   net.IPv4(byte(i1), byte(i2), byte(i3), byte(i4)),
//      }
//
//      m := new(dns.Msg)
//          m.Answer = append(m.Answer, rr)
//      return m
//    }


    return null;
  }

  //  func (s DockerDnsSolver) getMsg(key string, question dns.Question) *dns.Msg {
//    ip := s.c.Get(key).(string)
//        ipArr := strings.Split(ip, ".")
//    i1, _ := strconv.Atoi(ipArr[0])
//    i2, _ := strconv.Atoi(ipArr[1])
//    i3, _ := strconv.Atoi(ipArr[2])
//    i4, _ := strconv.Atoi(ipArr[3])
//
//    rr := &dns.A{
//      Hdr: dns.RR_Header{Name: question.Name, Rrtype: dns.TypeA, Class: dns.ClassINET, Ttl: 0},
//      A:   net.IPv4(byte(i1), byte(i2), byte(i3), byte(i4)),
//    }
//
//    m := new(dns.Msg)
//        m.Answer = append(m.Answer, rr)
//    return m
//  }


  @Override
  public byte priority() {
    return Priority.ONE;
  }
}
