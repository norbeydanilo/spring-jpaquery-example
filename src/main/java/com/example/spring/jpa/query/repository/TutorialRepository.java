package com.example.spring.jpa.query.repository;

import java.util.Date;
import java.util.List;

//import javax.xml.crypto.Data;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.example.spring.jpa.query.model.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {

  /*
   * Spring JPA soporta tanto JPQL como Native Query.
   * 
   * El Jakarta Persistence Query Language (JPQL; anteriormente Java Persistence
   * Query Language) es un lenguaje de consulta orientado a objetos
   * independiente de la plataforma definido como parte de la especificación
   * Jakarta Persistence (JPA; anteriormente Java Persistence API)-Wikipedia
   * 
   * JPQL está inspirado en SQL, y sus consultas se asemejan a las consultas SQL
   * en la sintaxis, pero operan contra objetos de entidad JPA
   * almacenados en una base de datos relacional en lugar de directamente con
   * tablas de bases de datos.
   */

  /*
   * JPQL sólo soporta un subconjunto de SQL estándar. Si desea realizar consultas
   * complejas, eche un vistazo a Native SQL Query.
   * Así es como se ejecuta una consulta SQL en Spring Boot con la
   * anotación @Query:
   * 
   * @Query(value = "SELECT * FROM tutorials", nativeQuery = true)
   * List<Tutorial> findAllNative();
   * 
   * @Query(value = "SELECT * FROM tutorials t WHERE t.published=true",
   * nativeQuery = true)
   * List<Tutorial> findByPublishedNative();
   */

  /*
   * Debe tener en cuenta que:
   * - Spring Data JPA no ajusta la consulta al dialecto SQL específico de la base
   * de datos, así que asegúrese de que la sentencia proporcionada está soportada
   * por el RDBMS.
   * - Spring Data JPA no soporta actualmente la ordenación dinámica para
   * consultas nativas, porque tendría que manipular la consulta real declarada,
   * lo que no puede hacer de forma fiable para SQL nativo.
   */

  /*
   * Por ejemplo, no podemos utilizar la ordenación dinámica en el siguiente
   * método:
   * 
   * 
   * // JPQL: ok
   * 
   * @Query("SELECT * FROM tutorials t WHERE t.title LIKE %?1%")
   * List<Tutorial> findByTitleAndSort(String title, Sort sort);
   * 
   * // Native query: throw InvalidJpaQueryMethodException
   * 
   * @Query(value = "SELECT * FROM tutorials t WHERE t.title LIKE %?1%",
   * nativeQuery = true)
   * List<Tutorial> findByTitleAndSortNative(String title, Sort sort);
   */

  /*
   * Utilicemos la anotación @Query para crear una consulta Spring JPA con las
   * palabras clave SELECT y WHERE.
   */
  @Query("SELECT t FROM Tutorial t")
  List<Tutorial> findAll();

  @Query("SELECT t FROM Tutorial t WHERE t.published=?1")
  List<Tutorial> findByPublished(boolean isPublished);

  @Query("SELECT t FROM Tutorial t WHERE t.title LIKE %?1%")
  List<Tutorial> findByTitleLike(String title);

  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', ?1,'%'))")
  List<Tutorial> findByTitleLikeCaseInsensitive(String title);

  /*
   * Consulta JPA de Spring Data para actualizar una entidad utilizando @Query
   * junto con @Transactional y @Modifying:
   */
  @Transactional
  @Modifying
  @Query("UPDATE Tutorial t SET t.published=true WHERE t.id=?1")
  int publishTutorial(Long id);

  /*
   * Consulta JPA de Spring Data para fecha/columna mayor que o igual a:
   */
  @Query("SELECT t FROM Tutorial t WHERE t.level >= ?1")
  List<Tutorial> findByLevelGreaterThanEqual(int level);

  @Query("SELECT t FROM Tutorial t WHERE t.createdAt >= ?1")
  List<Tutorial> findByDateGreaterThanEqual(Date date);

  /*
   * Consulta JPA de Spring Data entre fecha/columna:
   */
  @Query("SELECT t FROM Tutorial t WHERE t.level BETWEEN ?1 AND ?2")
  List<Tutorial> findByLevelBetween(int start, int end);

  @Query("SELECT t FROM Tutorial t WHERE t.createdAt BETWEEN ?1 AND ?2")
  List<Tutorial> findByDateBetween(Date start, Date end);

  /*
   * En el ejemplo anterior, utilizamos Parámetros Posicionales: los parámetros se
   * referencian por sus posiciones en la consulta (definidas utilizando ? seguido
   * de un número (?1, ?2, ...). Spring Data JPA sustituirá automáticamente el
   * valor de cada parámetro en la misma posición.
   * 
   * Otra forma de vincular valores son los parámetros con nombre. Un parámetro
   * con nombre empieza por : seguido del nombre del parámetro (:title, :date,
   * ...). Por ejemplo:
   */
  @Query("SELECT t FROM Tutorial t WHERE t.published=:isPublished AND t.level BETWEEN :start AND :end")
  List<Tutorial> findByLevelBetween(@Param("start") int start, @Param("end") int end,
      @Param("isPublished") boolean isPublished);

  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword,'%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword,'%'))")
  List<Tutorial> findByTitleContainingOrDescriptionContainingCaseInsensitive(String keyword);

  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title,'%')) AND t.published=:isPublished")
  List<Tutorial> findByTitleContainingCaseInsensitiveAndPublished(String title, boolean isPublished);

  /*
   * Ejemplo de columna Order By de Spring Data JPA Query con filtrado:
   */
  @Query("SELECT t FROM Tutorial t ORDER BY t.level DESC")
  List<Tutorial> findAllOrderByLevelDesc();

  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', ?1,'%')) ORDER BY t.level ASC")
  List<Tutorial> findByTitleOrderByLevelAsc(String title);

  @Query("SELECT t FROM Tutorial t WHERE t.published=true ORDER BY t.createdAt DESC")
  List<Tutorial> findAllPublishedOrderByCreatedDesc();

  /*
   * Ejemplo de consulta JPA de Spring Data utilizando la clase Sort con filtrado:
   */
  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', ?1,'%'))")
  List<Tutorial> findByTitleAndSort(String title, Sort sort);

  @Query("SELECT t FROM Tutorial t WHERE t.published=?1")
  List<Tutorial> findByPublishedAndSort(boolean isPublished, Sort sort);

  /*
   * Ejemplo de consulta JPA de Spring Data utilizando la clase Pageable para la
   * paginación (con ordenación y filtrado):
   */
  // Pagination and Sorting with Pageable
  @Query("SELECT t FROM Tutorial t")
  Page<Tutorial> findAllWithPagination(Pageable pageable);

  @Query("SELECT t FROM Tutorial t WHERE t.published=?1")
  Page<Tutorial> findByPublishedWithPagination(boolean isPublished, Pageable pageable);

  @Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', ?1,'%'))")
  Page<Tutorial> findByTitleWithPagination(String title, Pageable pageable);
}