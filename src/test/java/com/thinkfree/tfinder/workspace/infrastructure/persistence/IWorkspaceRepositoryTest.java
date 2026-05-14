package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@DataJpaTest
@ActiveProfiles("test")
class IWorkspaceRepositoryTest {

    @Autowired
    private IWorkspaceRepository workspaceRepository;
    @Autowired
    private EntityManager entityManager;

    private final FixtureMonkey fixture = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

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
        entityManager.persist(workspace);

        //then
        assertThrows(NoSuchElementException.class, () -> workspaceRepository.findById(id));

    }


}
