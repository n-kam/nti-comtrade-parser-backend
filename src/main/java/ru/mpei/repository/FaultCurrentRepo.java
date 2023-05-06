package ru.mpei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mpei.model.FaultModel;

public interface FaultCurrentRepo extends JpaRepository<FaultModel, Long> {
}
