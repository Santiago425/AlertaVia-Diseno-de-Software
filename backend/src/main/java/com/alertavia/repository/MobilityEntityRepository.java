package com.alertavia.repository;

import com.alertavia.model.MobilityEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MobilityEntityRepository extends MongoRepository<MobilityEntity, String> {

    List<MobilityEntity> findAllByOrderByNameAsc();
}
