package net.n2oapp.security.admin.impl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Абстрактная сущность Пользователя
 * Выделена для возможности переодпределять в прикладных приложениях
 */

@MappedSuperclass
public abstract class AbstractUserEntity implements Serializable {

}
