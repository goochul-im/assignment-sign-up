package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.workspace.domain.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.domain.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DataJpaTest
@ActiveProfiles("test")
class IWorkspaceRepositoryTest {

    @Autowired
    private IWorkspaceRepository workspaceRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void delete_대신_소프트_딜리트가_발생한다() {
        //given
        WorkspaceEntity workspace = workspaceRepository.save(new WorkspaceEntity(
                "testWorksapce",
                "testWorkspace"
        ));
        Long id = workspace.getId();
        System.out.println("id : " + id);

        //when
        workspace.delete();
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(workspaceRepository.findById(id)).isEmpty();
    }


}
