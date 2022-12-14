package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.authentication.service.JwtUtils;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<Carton> cartons = iCartonRepo.findAll();
        ResponseCartonDTO responseCartonDTO;
        List<ResponseCartonDTO> responseUnitDTOList = new ArrayList<>();

        for(Carton item: cartons) {
            Users storeKeeper = new Users();
            Storehouse storehouse = new Storehouse();

            if (item.getIdStoreKeeper() != null)
                storeKeeper = iUserService.getByInternalReference(item.getIdStoreKeeper());
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

        Page<ResponseCartonDTO> cartonPage = new PageImpl<>(responseUnitDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseUnitDTOList.size());
        return cartonPage;
    }

    @Override
    public Page<Carton> getCartonsByIdStoreHouse(Long idStoreHouse, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreHouse(idStoreHouse,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Carton> getCartonsByIdStoreKeeper(Long idStoreKeeper, int page, int size, String sort, String order) {
        return iCartonRepo.getCartonsByIdStoreKeeper(idStoreKeeper,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Carton> getCartonById(Long id) {
        return iCartonRepo.findById(id);
    }

    @Override
    public Map<String, Object> createCarton(Carton carton, Long idStoreHouseSell, int diffCarton) {
        boolean saveCarton = (carton.getId() == null)? true: false;
        iCartonRepo.save(carton);
        if(saveCarton)
            generateCoupon(carton, idStoreHouseSell, diffCarton);
        Map<String, Object> cartonEncoded = new HashMap<>();
        cartonEncoded.put("carton", carton);
        return cartonEncoded;
    }

    @Async
    @Transactional
    void generateCoupon(Carton carton, Long idStoreHouseSell, int diffCarton){

        TypeVoucher typeVoucher = iTypeVoucherService.getTypeVoucherByAmountEquals(carton.getTypeVoucher()).get();
        List<Notebook> notebookList = new ArrayList<>();
        List<Coupon> couponList = new ArrayList<>();
        int numberCouponP = Integer.parseInt(numberCoupon);
        int numberCouponByCarton = (carton.getFrom()-carton.getTo()+1)/numberCouponP;
        int x = carton.getTo();
        for(int i=carton.getTo(); i< carton.getTo()+numberCouponByCarton; i++){
            System.out.println(" i "+ i);
            Notebook notebook = new Notebook();
            notebook.setInternalReference(jwtUtils.generateInternalReference());
            notebook.setCreatedAt(LocalDateTime.now());
            notebook.setSerialNumber("DE "+carton.getTypeVoucher()+"-"+carton.getSerialTo()+"  "+"A "+carton.getTypeVoucher()+"-"+carton.getSerialFrom()+" ");
            notebook.setIdCarton(carton.getInternalReference());
            notebook.setIdStoreHouse(idStoreHouseSell);
            notebook.setIdStoreKeeper(carton.getIdStoreKeeper());
            Status status = iStatusRepo.findByName(EStatus.AVAILABLE).orElseThrow(()-> new ResourceNotFoundException("Statut:  "  +  EStatus.AVAILABLE +  "  not found"));
            notebook.setStatus(status);
            if (typeVoucher != null)
                notebook.setIdTypeVoucher(typeVoucher.getInternalReference());
//            Map<String, Object> notebookEncoded = new HashMap<>();
//            notebookEncoded = iNotebookService.createNotebook(notebook);
//            notebook = (Notebook) notebookEncoded.get("notebook");
            notebookList.add(notebook);
            for(int y=0; y<numberCouponP; y++){
                x++;
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
                couponList.add(coupon);
//                iCouponService.createCoupon(coupon);
            }
        }
        iNotebookService.createAllNotebook(notebookList);
        iCouponService.createAllCoupon(couponList);

        Item item = new Item();
        Unit unit = new Unit();
        StockMovement stockMovement = new StockMovement();
        Storehouse storehouse = iStorehouseService.getByInternalReference(carton.getIdStoreHouse()).get();

        item.setQuantityCarton(diffCarton);
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
        emailProps.put("quantityCarton", diffCarton);
        emailProps.put("typevoucher", typeVoucher.getAmount());
        emailProps.put("quantityNotebook", notebookList.size());
        emailProps.put("quantityCoupon", couponList.size());
        emailProps.put("storehouse", storehouse.getInternalReference()+" - "+storehouse.getType()+" - "+storehouse.getName());
        emailProps.put("store", store.getInternalReference()+" - "+store.getLocalization());

        if(carton.getIdStoreKeeper() != null){
            Users storeKeeper = iUserService.getByInternalReference(carton.getIdStoreKeeper());
            if(diffCarton > 0)
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ORDER_STOCKAGE, ApplicationConstant.TEMPLATE_EMAIL_ORDER_STOCKAGE));
            else
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, storeKeeper.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_ORDER_SUPPLY, ApplicationConstant.TEMPLATE_EMAIL_ORDER_SUPPLY));
        }

    }


    @Override
    public void deleteCarton(Carton carton) {
        iCartonRepo.delete(carton);
    }
}
