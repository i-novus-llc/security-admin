package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.NotificationClientModel;

public interface NotificationService {
    Integer createClient(NotificationClientModel clientModel);
}
