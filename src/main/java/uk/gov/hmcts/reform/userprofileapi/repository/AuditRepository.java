package uk.gov.hmcts.reform.userprofileapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.userprofileapi.domain.entities.Audit;
import uk.gov.hmcts.reform.userprofileapi.domain.entities.UserProfile;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    @Transactional(readOnly = true)
    Optional<Audit> findByUserProfile(UserProfile userProfile);

    List<Audit> findAllByUserProfile(UserProfile userProfile);

}
