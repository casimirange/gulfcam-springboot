package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.client.entity.Client;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import com.gulfcam.fuelcoupon.globalConfiguration.ApplicationConstant;
import com.gulfcam.fuelcoupon.order.dto.ResponseUnitDTO;
import com.gulfcam.fuelcoupon.order.entity.Item;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.entity.Unit;
import com.gulfcam.fuelcoupon.order.service.IItemService;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.order.service.IUnitService;
import com.gulfcam.fuelcoupon.store.dto.ResponseCartonDTO;
import com.gulfcam.fuelcoupon.store.entity.*;
import com.gulfcam.fuelcoupon.store.repository.ICartonRepo;
import com.gulfcam.fuelcoupon.store.service.*;
import com.gulfcam.fuelcoupon.user.dto.EmailDto;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.user.service.IEmailService;
import com.gulfcam.fuelcoupon.user.service.IUserService;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import com.gulfcam.fuelcoupon.utilities.repository.IStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.data.jpa.domain.Specification;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
<<<<<<< HEAD
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class CartonServiceImpl implements ICartonService {

    @Autowired
    ICartonRepo iCartonRepo;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IEmailService emailService;

    @Autowired
    ICouponService iCouponService;

    @Autowired
    INotebookService iNotebookService;

    @Autowired
    IStatusRepo iStatusRepo;

    @Autowired
    ITypeVoucherService iTypeVoucherService;

    @Autowired
    IStorehouseService iStorehouseService;

    @Autowired
    IStoreService iStoreService;

    @Autowired
    IItemService iItemService;

    @Autowired
    IStockMovementService iStockMovementService;

    @Autowired
    IUnitService iUnitService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Value("${app.typeStockage}")
    String typeStockage;
    @Value("${app.typeAppro}")
    String typeAppro;
    @Value("${app.typeTransfert}")
    String typeTransfert;
    @Value("${app.numberCoupon}")
    String numberCoupon;

    @Override
    public Optional<Carton> getByInternalReference(Long internelReference) {
        return iCartonRepo.getCartonByInternalReference(internelReference);
    }

    @Override
    public Page<ResponseCartonDTO> getAllCartons(int page, int size, String sort, String order) {
        Page<Carton> cartons = iCartonRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseCartonDTO responseCartonDTO;
        List<ResponseCartonDTO> responseUnitDTOList = new ArrayList<>();

        for(Carton item: cartons) {
            Users storeKeeper = new Users();
            Storehouse storehouse = new Storehouse();

            if (item.getIdSpaceManager1() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdSpaceManager1());
            if (item.getIdStoreHouse() != null)
                storehouse = iStorehouseService.getByInternalReference(item.getIdStoreHouse()).get();

            responseCartonDTO = new ResponseCartonDTO();
            responseCartonDTO.setId(item.getId());
            responseCartonDTO.setStoreHouse(storehouse);
            responseCartonDTO.setStoreKeeper(storeKeeper);
            responseCartonDTO.setStatus(item.getStatus());
            responseCartonDTO.setCreatedAt(item.getCreatedAt());
            responseCartonDTO.setInternalReference(item.getInternalReference());
            responseCartonDTO.setFrom(item.getFrom());
            responseCartonDTO.setTo(item.getTo());
            responseCartonDTO.setSerialFrom(item.getSerialFrom());
            responseCartonDTO.setNumber(item.getNumber());
            responseCartonDTO.setSerialTo(item.getSerialTo());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setUpdateAt(item.getUpdateAt());
            responseCartonDTO.setNameStoreHouse((storehouse != null)? storehouse.getName(): "");
            responseUnitDTOList.add(responseCartonDTO);
        }

        Page<ResponseCartonDTO> cartonPage = new PageImpl<>(responseUnitDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), iCartonRepo.findAll().size());
        return cartonPage;
    }

    @Override
<<<<<<< HEAD
<<<<<<< HEAD
    public Page<ResponseCartonDTO> filtres(String number, String statusName, String idStoreHouse, LocalDate date, String spaceManager1, String type, int page, int size, String sort, String order) {
        Specification<Carton> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (number != null && !number.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("number").as(String.class)), "%" + number + "%"));
            }

            if (idStoreHouse != null && !idStoreHouse.isEmpty()){
                Storehouse storehouse = iStorehouseService.getByInternalReference(Long.parseLong(idStoreHouse)).get();

                Long ref = !storehouse.getInternalReference().toString().isEmpty() ? storehouse.getInternalReference() : 0;
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idStoreHouse")), ref));
            }

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), date.toString() + '%'));
            }

            if (type != null && !type.isEmpty()){
//                Optional<TypeVoucher> typeVoucher = iTypeVoucherService.getByInternalReference(Long.parseLong(type));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeVoucher")), Integer.parseInt(type)));
            }

            if (spaceManager1 != null && !spaceManager1.isEmpty()){
//                Users users = iUserService.getByInternalReference(Long.parseLong(spaceManager1));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("idSpaceManager1")), Long.parseLong(spaceManager1)));
            }

            if (statusName != null && !statusName.isEmpty()){
                Status status = iStatusRepo.findByName(EStatus.valueOf(statusName.toUpperCase())).get();
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        Page<Carton> cartons = iCartonRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

        ResponseCartonDTO responseCartonDTO;
        List<ResponseCartonDTO> responseUnitDTOList = new ArrayList<>();

        for(Carton item: cartons) {
            Users storeKeeper = new Users();
            Storehouse storehouse1 = new Storehouse();

            if (item.getIdSpaceManager1() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdSpaceManager1());
            if (item.getIdStoreHouse() != null)
                storehouse1 = iStorehouseService.getByInternalReference(item.getIdStoreHouse()).get();

            responseCartonDTO = new ResponseCartonDTO();
            responseCartonDTO.setId(item.getId());
            responseCartonDTO.setStoreHouse(storehouse1);
            responseCartonDTO.setStoreKeeper(storeKeeper);
            responseCartonDTO.setStatus(item.getStatus());
            responseCartonDTO.setCreatedAt(item.getCreatedAt());
            responseCartonDTO.setInternalReference(item.getInternalReference());
            responseCartonDTO.setFrom(item.getFrom());
            responseCartonDTO.setTo(item.getTo());
            responseCartonDTO.setSerialFrom(item.getSerialFrom());
            responseCartonDTO.setNumber(item.getNumber());
            responseCartonDTO.setSerialTo(item.getSerialTo());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setTypeVoucher(item.getTypeVoucher());
            responseCartonDTO.setUpdateAt(item.getUpdateAt());
            responseCartonDTO.setNameStoreHouse((storehouse1 != null)? storehouse1.getName(): "");
            responseUnitDTOList.add(responseCartonDTO);
        }

        return new PageImpl<>(responseUnitDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), cartons.getTotalElements());
    }

    @Override
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    public Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public List<Carton> getCartonsByIdStoreHouse(Long idStoreHouse) {
        return iCartonRepo.getCartonsByIdStoreHouse(idStoreHouse);
    }

    @Override
    public Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreKeeper(idStoreKeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Carton> getCartonsByIdSpaceManager1(Long idSpaceManager1, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdSpaceManager1(idSpaceManager1,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Carton> getCartonById(Long id) {
        return iCartonRepo.findById(id);
    }

    @Override
    public Map<String, Object> createCarton(Carton carton, int diffCarton) {

        iCartonRepo.save(carton);

        if(diffCarton != 0){
            Storehouse storehouse = iStorehouseService.getByInternalReference(carton.getIdStoreHouse()).get();
            TypeVoucher typeVoucher = iTypeVoucherService.getTypeVoucherByAmountEquals(carton.getTypeVoucher()).get();
            Store store = iStoreService.getByInternalReference(storehouse.getIdStore()).get();
            Item item = new Item();
            item.setQuantityCarton(diffCarton);
            item.setIdTypeVoucher(typeVoucher.getInternalReference());
            item.setQuantityNotebook(0);
            item.setIdStoreHouse(carton.getIdStoreHouse());
            item.setInternalReference(jwtUtils.generateInternalReference());
            Status statusItem = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
            item.setStatus(statusItem);
            item.setCreatedAt(LocalDateTime.now());

            iItemService.createItem(item);

            StockMovement stockMovement = new StockMovement();
            stockMovement.setType(typeStockage);
            stockMovement.setIdStoreHouse2(storehouse.getInternalReference());
            stockMovement.setIdStoreHouse1(storehouse.getInternalReference());
            stockMovement.setIdStore2(storehouse.getIdStore());
            stockMovement.setIdStore1(storehouse.getIdStore());
            stockMovement.setInternalReference(jwtUtils.generateInternalReference());
            stockMovement.setCreatedAt(LocalDateTime.now());
            iStockMovementService.createStockMovement(stockMovement);

            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("carton", carton.getInternalReference()+"DE "+carton.getTypeVoucher()+"-"+carton.getSerialTo()+"  "+"A "+carton.getTypeVoucher()+"-"+carton.getSerialFrom()+" ");
            emailProps.put("quantityCarton", diffCarton);
            emailProps.put("typevoucher", typeVoucher.getAmount());
            emailProps.put("quantityNotebook", 0);
            emailProps.put("quantityCoupon", 0);
            emailProps.put("storehouse", storehouse.getInternalReference()+" - "+storehouse.getType()+" - "+storehouse.getName());
            emailProps.put("store", store.getInternalReference()+" - "+store.getLocalization());
            if(carton.getIdSpaceManager1() != null){
                Users storeKeeper = iUserService.getByInternalReference(carton.getIdSpaceManager1());
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ORDER_STOCKAGE, ApplicationConstant.TEMPLATE_EMAIL_ORDER_STOCKAGE));
            }
        }


        Map<String, Object> cartonEncoded = new HashMap<>();
        cartonEncoded.put("carton", carton);
        return cartonEncoded;
    }


    @Override
    public Map<String, Object> supplyStoreHouse(Carton carton, Storehouse storehouse) {

        iCartonRepo.save(carton);

        TypeVoucher typeVoucher = iTypeVoucherService.getTypeVoucherByAmountEquals(carton.getTypeVoucher()).get();

        Item item = new Item();
        item.setQuantityCarton(-1);
        item.setIdTypeVoucher(typeVoucher.getInternalReference());
        item.setQuantityNotebook(0);
        item.setIdStoreHouse(carton.getIdStoreHouse());
        item.setInternalReference(jwtUtils.generateInternalReference());
        Status statusItem = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(statusItem);
        item.setCreatedAt(LocalDateTime.now());
        iItemService.createItem(item);


        generateCoupon(carton, storehouse.getInternalReference());
        Map<String, Object> cartonEncoded = new HashMap<>();
        cartonEncoded.put("carton", carton);
        return cartonEncoded;
    }

    @Async
    @Transactional
    void generateCoupon(Carton carton, Long idStoreHouseSell){

        TypeVoucher typeVoucher = iTypeVoucherService.getTypeVoucherByAmountEquals(carton.getTypeVoucher()).get();
        List<Notebook> notebookList = new ArrayList<>();
        List<Coupon> couponList = new ArrayList<>();
        int numberCouponP = Integer.parseInt(numberCoupon);
        int numberCouponByCarton = (carton.getFrom()-carton.getTo()+1)/numberCouponP;
        int x = carton.getTo();
        for(int i=carton.getTo(); i<carton.getTo()+numberCouponByCarton; i++){
            Notebook notebook = new Notebook();
            notebook.setInternalReference(jwtUtils.generateInternalReference());
            notebook.setCreatedAt(LocalDateTime.now());
            notebook.setPlage_coupon("");
            notebook.setSerialNumber("DE "+carton.getTypeVoucher()+"-"+carton.getSerialTo()+"  "+"A "+carton.getTypeVoucher()+"-"+carton.getSerialFrom()+" ");
            notebook.setIdCarton(carton.getInternalReference());
            notebook.setIdStoreHouse(idStoreHouseSell);
            notebook.setIdStoreKeeper(carton.getIdSpaceManager1());
            Status status = iStatusRepo.findByName(EStatus.AVAILABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.AVAILABLE +  "  not found"));
            notebook.setStatus(status);
            if (typeVoucher != null)
                notebook.setIdTypeVoucher(typeVoucher.getInternalReference());

            notebookList.add(notebook);
            for(int y=0; y<numberCouponP; y++){
                Coupon coupon = new Coupon();
                Long ref = jwtUtils.generateInternalReference();
                while (iCouponService.existsCouponByInternalReference(ref)){
                    ref = jwtUtils.generateInternalReference();
                }
                coupon.setInternalReference(ref);
                coupon.setCreatedAt(LocalDateTime.now());
                coupon.setIdNotebook(notebook.getInternalReference());
                coupon.setSerialNumber(x+"");
                if (typeVoucher != null)
                    coupon.setIdTypeVoucher(typeVoucher.getInternalReference());
                Status statusCoupon = iStatusRepo.findByName(EStatus.AVAILABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.AVAILABLE +  "  not found"));
                coupon.setStatus(statusCoupon);
                x++;
                couponList.add(coupon);
            }
        }
        iNotebookService.createAllNotebook(notebookList);
        iCouponService.createAllCoupon(couponList);

        Item item = new Item();
        Unit unit = new Unit();
        StockMovement stockMovement = new StockMovement();
        Storehouse storehouse = iStorehouseService.getByInternalReference(carton.getIdStoreHouse()).get();

        item.setQuantityCarton(0);
        item.setIdTypeVoucher(typeVoucher.getInternalReference());
        item.setQuantityNotebook(notebookList.size());
        item.setIdStoreHouse(idStoreHouseSell);
        item.setInternalReference(jwtUtils.generateInternalReference());
        Status statusItem = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        item.setStatus(statusItem);
        item.setCreatedAt(LocalDateTime.now());

        iItemService.createItem(item);

        unit.setIdTypeVoucher(typeVoucher.getInternalReference());
        unit.setQuantityNotebook(notebookList.size());
        unit.setIdStore(storehouse.getIdStore());
        unit.setInternalReference(jwtUtils.generateInternalReference());
        Status statusUnit = iStatusRepo.findByName(EStatus.CREATED).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.CREATED +  "  not found"));
        unit.setStatus(statusUnit);
        unit.setCreatedAt(LocalDateTime.now());

        iUnitService.createUnit(unit);

        stockMovement.setType(typeAppro);
        stockMovement.setIdStoreHouse2(idStoreHouseSell);
        stockMovement.setIdStoreHouse1(storehouse.getInternalReference());
        stockMovement.setIdStore2(storehouse.getIdStore());
        stockMovement.setIdStore1(storehouse.getIdStore());
        stockMovement.setInternalReference(jwtUtils.generateInternalReference());
        stockMovement.setCreatedAt(LocalDateTime.now());
        iStockMovementService.createStockMovement(stockMovement);

        Store store = iStoreService.getByInternalReference(storehouse.getIdStore()).get();

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("carton", carton.getInternalReference()+"DE "+carton.getTypeVoucher()+"-"+carton.getSerialTo()+"  "+"A "+carton.getTypeVoucher()+"-"+carton.getSerialFrom()+" ");
        emailProps.put("quantityCarton", 0);
        emailProps.put("typevoucher", typeVoucher.getAmount());
        emailProps.put("quantityNotebook", notebookList.size());
        emailProps.put("quantityCoupon", couponList.size());
        emailProps.put("storehouse", storehouse.getInternalReference()+" - "+storehouse.getType()+" - "+storehouse.getName());
        emailProps.put("store", store.getInternalReference()+" - "+store.getLocalization());

        if(carton.getIdSpaceManager1() != null){
            Users storeKeeper = iUserService.getByInternalReference(carton.getIdSpaceManager1());
            emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ORDER_SUPPLY, ApplicationConstant.TEMPLATE_EMAIL_ORDER_SUPPLY));
        }

    }


    @Override
    public void deleteCarton(Carton carton) {
        iCartonRepo.delete(carton);
    }
}
