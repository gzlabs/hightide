package io.hightide.db;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import resources.Domain;
import resources.Post;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static io.hightide.db.PersistenceSupport.$;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceSupportTest {

    @Test
    public void a_testSaveEntityUsingStaticMethod() {
        Post post = new Post();
        post.setId(1l);
        post.setTitle("A Title");
        post.setBody("And body");
        assertNotNull($(Post.class).save(post));
    }

    @Test
    public void b_testGetEntity() {
        Post post = $(Post.class).get(1L);
        assertNotNull(post);
    }

    @Test
    public void c_testSaveEntityUsingInstanceMethod() {
        Post post = new Post();
        post.setId(3l);
        post.setTitle("A Title");
        post.setBody("And body");
        assertNotNull($(post).save());
    }

    @Test
    public void d_testFindAllEntities() {
        List<Post> posts = $(Post.class).all();
        assertThat(posts).hasSize(2);
    }

    @Test
    public void e_testDeleteEntityUsingStaticMethod() {
        $(Post.class).delete(1L);
    }

    @Test
    public void f_testDeleteEntityUsingInstanceMethod() {
        Post post = $(Post.class).get(3L);
        $(post).delete();
    }

    @Test
    public void g_testMysqlDatabaseSave() {
        Domain domain = new Domain();
        domain.setId(1L);
        domain.setStr("A string to come");
        $(Domain.class).save(domain);
    }

    @Test
    public void h_testMysqlDatabaseGet() {
        Domain domain = $(Domain.class).get(1L);
        assertThat(domain).isNotNull();
    }

    @Test
    public void i_testDefaultDatabaseNotHavingMysqlData() {
        try {
            Domain domain = $("default", Domain.class).get(1L);
            fail("Should throw EntityNotFoundException cause Domain entity doesn't exist on 'default' database.");
        } catch (EntityNotFoundException e) {
            assertThat(e).hasMessage("Entity resources.Domain not found on database default.");
        }
    }
}
