package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;


/**
 * TODO Sprint add-controllers.
 */
@Data // аннотация, которая содержит в себе возможности из @ToString, @EqualsAndHashCode, @Getter / @Setter и @RequiredArgsConstructor.
@AllArgsConstructor //генерирует конструктор с одним параметром для каждого поля в классе. Поля, помеченные @NonNull, имеют проверку на null для этих параметров.
//@NoArgsConstructor //генерирует конструктор без параметров. Если это невозможно (потому что поля final), то возникает ошибка компиляции.
@RequiredArgsConstructor
@Entity //помечают классы-сущности. В процессе работы ORM-фреймворк, например, Hibernate, будет автоматически создавать экземпляры этого класса и записывать данные в поля каждого экземпляра
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //https://habr.com/ru/company/haulmont/blog/653843/
    //Если название поля в модели отличается от имени поля в базе, нужно обязательно указать маппинг между ними с помощью аннотации @Column.
    private long id;
    private String name;
    private String email;

}
