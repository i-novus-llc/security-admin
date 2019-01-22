package net.n2oapp.security.admin.impl.service;


import net.n2oapp.security.admin.api.criteria.BankCriteria;
import net.n2oapp.security.admin.api.model.bank.Bank;
import net.n2oapp.security.admin.api.model.bank.BankCreateForm;
import net.n2oapp.security.admin.api.model.bank.BankForm;
import net.n2oapp.security.admin.api.model.bank.BankUpdateForm;
import net.n2oapp.security.admin.api.service.BankService;
import net.n2oapp.security.admin.impl.entity.BankEntity;
import net.n2oapp.security.admin.impl.repository.BankRepository;
import net.n2oapp.security.admin.impl.service.specification.BankSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Реализация сервиса управления сведениями о банке
 */
@Service
@Transactional
public class BankServiceImpl implements BankService {

     private  BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public Page<Bank> findAll(BankCriteria criteria) {
        final Specification<BankEntity> specification = new BankSpecifications(criteria);
        final Page<BankEntity> all = bankRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Bank getById(String id) {
        BankEntity entity = bankRepository.findOne(UUID.fromString(id));
        return model(entity);
    }

    @Override
    public Bank create(BankCreateForm bank) {
        validate(bank);
        if (Boolean.TRUE.equals(bank.getIsCoincidesAddress())) {
            bank.setActualAddress(bank.getLegalAddress());
        }
        BankEntity entity = entityForm(new BankEntity(), bank);
        BankEntity savedBank = bankRepository.save(entity);
        return model(savedBank);
    }

    @Override
    public Bank update(BankUpdateForm bank) {
        validate(bank);
        if (Boolean.TRUE.equals(bank.getIsCoincidesAddress())) {
            bank.setActualAddress(bank.getLegalAddress());
        }
        Bank model = getById(bank.getId().toString());
        /*if (model.getLastActionDate().truncatedTo(ChronoUnit.MILLIS).compareTo(bank.getLastActionDate()) != 0) {
            throw new OptimisticLockException(String.format("Сущность \"%s\" с идентификатором %s была изменена. " +
                    "Пожалуйста, обновите страницу и попробуйте снова", bank.getClass().getSimpleName(), bank.getId()));
        }*/

        BankEntity entity = entityForm(new BankEntity(), bank);
        entity.setId(bank.getId());
        entity.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
        BankEntity savedBank = bankRepository.save(entity);
        return model(savedBank);
    }

    private Bank model(BankEntity entity) {
        if (entity == null) return null;
        Bank model = new Bank();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setRegNum(entity.getRegNum());
        model.setRegDt(entity.getRegDt());
        model.setInn(entity.getInn());
        model.setOgrn(entity.getOgrn());
        model.setKpp(entity.getKpp());
        model.setBik(entity.getBik());
        model.setActualAddress(entity.getActualAddress());
        model.setLegalAddress(entity.getLegalAddress());
        model.setLastActionDate(entity.getLastActionDate());
        model.setCreationDate(entity.getCreationDate());
        if(entity.getParent()!=null){
            Bank parent = new Bank();
            parent.setShortName(entity.getParent().getShortName());
            parent.setId(entity.getParent().getId());
            model.setParent(parent);
        }
        return model;
    }

    private BankEntity entityForm(BankEntity entity, BankForm model) {
        entity.setFullName(model.getFullName());
        entity.setShortName(model.getShortName());
        entity.setRegNum(model.getRegNum());
        entity.setRegDt(model.getRegDt());
        entity.setInn(model.getInn());
        entity.setOgrn(model.getOgrn());
        entity.setKpp(model.getKpp());
        entity.setBik(model.getBik());
        entity.setActualAddress(model.getActualAddress());
        entity.setLegalAddress(model.getLegalAddress());
        if(model.getParent()!=null) {
            entity.setParent(bankRepository.findOne(model.getParent().getId()));
        }
        return entity;
    }
    private void validate(BankForm bank) {
        requireNonNull(bank.getFullName());
        requireNonNull(bank.getShortName());
        requireNonNull(bank.getOgrn());
        requireNonNull(bank.getInn());
        requireNonNull(bank.getKpp());
        requireNonNull(bank.getLegalAddress());
    }
}
