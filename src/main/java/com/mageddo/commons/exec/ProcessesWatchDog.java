package com.mageddo.commons.exec;

import com.mageddo.commons.lang.Singletons;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public class ProcessesWatchDog {

  private List<Supplier<Process>> processes = new ArrayList<>();

  public static ProcessesWatchDog instance() {
    return Singletons.createOrGet(ProcessesWatchDog.class, ProcessesWatchDog::new);
  }

  public void watch(Supplier<Process> sup) {
    this.processes.add(sup);
  }

  public void watch(Process process) {
    this.processes.add(() -> process);
  }

  public void killAllProcesses() {
    final var validProcesses = this.findValidProcesses();

    log.debug("status=killing all processes, processes={}, valid={}", this.processes.size(), validProcesses.size());

    validProcesses.forEach(process -> {
      try {
        process.destroy();
        log.trace("status=killed, pid={}", process.pid());
      } catch (Exception e) {
        log.warn("status=unable to destroy, processId={}, msg={}", process.pid(), e.getMessage(), e);
      }
    });
  }

  private List<Process> findValidProcesses() {
    return this.processes.stream()
      .map(Supplier::get)
      .filter(Objects::nonNull)
      .toList();
  }
}
