package com.gulfcam.fuelcoupon.globalConfiguration;

import javax.servlet.http.HttpServletRequest;

public class ApplicationConstant {

    public static final String ENTREPRISE_NAME = "GULFCAM";

    public static final String SUBJECT_EMAIL_PAY = "GulfCam - Accès espace membres";

    public static final String SUBJECT_EMAIL_OPT = "GulfCam - Code OTP";

    public static final String SUBJECT_EMAIL_NEW_USER = "GulfCam - Bienvenue";

    public static final String SUBJECT_EMAIL_NEW_ORDER = "GulfCam - Nouvelle Commande : #Ref";

    public static final String SUBJECT_EMAIL_ORDER_STOCKAGE = "GulfCam - Ordre de stockage";

    public static final String SUBJECT_EMAIL_ORDER_SUPPLY = "GulfCam - Ordre d'approvisionnement";

    public static final String SUBJECT_EMAIL_ORDER_TRANSFER= "GulfCam - Ordre de de transfert de cartons";

    public static final String SUBJECT_EMAIL_DEMANDE_OPPOSITION= "GulfCam - Demande d'opposition";

    public static final String SUBJECT_EMAIL_CREDIT_NOTE= "GulfCam - Note de credit";

    public static final String SUBJECT_EMAIL_ACCEPT_COUPON= "GulfCam - Acceptation du coupon ";

    public static final String SUBJECT_EMAIL_VALID_CREDIT_NOTE= "GulfCam - Validation d'une note de crédit ";

    public static final String SUBJECT_EMAIL_MODIFY_ORDER = "GulfCam - Modification de la Commande : #Ref";

    public static final String SUBJECT_EMAIL_CANCEL_ORDER = "GulfCam - Annulation de la Commande : #Ref";

    public static final String SUBJECT_EMAIL_CANCEL_MULTI_ORDER = "GulfCam - Annulation en lots des Commandes ";

    public static final String SUBJECT_EMAIL_NEW_INVOICE = "GulfCam - Nouvelle Proforma : #Ref";

    public static final String SUBJECT_EMAIL_NEW_INVOICE2 = "GulfCam - Nouvelle Préfacture : #Ref";

    public static final String SUBJECT_EMAIL_EXPORT_ORDERS_EXCEL = "GulfCam - Liste des commandes";

    public static final String SUBJECT_EMAIL_EXPORT_COUPONS_EXCEL = "GulfCam - Liste des coupons";

    public static final String SUBJECT_EMAIL_NEW_RECEIVED = "GulfCam - Reçue de paiement : #Ref";

    public static final String SUBJECT_EMAIL_NEW_FACTURE = "GulfCam - Facture : #Ref";

    public static final String SUBJECT_EMAIL_PAY_TEMPORAIRE = "GulfCam - Accès Temporaire";

    public static final String SUBJECT_EMAIL_VILID_PROFIL = "GulfCam - Validation de profil ";

    public static final String SUBJECT_EMAIL_VILID_PROFIL_NOT_VALIDE = "GulfCam - Profil non valide";

    public static final String TEMPLATE_EMAIL_ENTREPRISE_MEMBRE = "email-verification";

    public static final String TEMPLATE_EMAIL_NEW_USER = "new-user";

    public static final String TEMPLATE_EMAIL_NEW_ORDER = "new-order";

    public static final String TEMPLATE_EMAIL_ORDER_STOCKAGE = "order-stockage";

    public static final String TEMPLATE_EMAIL_ORDER_SUPPLY = "order-supply";

    public static final String TEMPLATE_EMAIL_ORDER_TRANSFER = "order-transfer";

    public static final String TEMPLATE_EMAIL_DEMANDE_OPPOSITION = "demande-opposition";

    public static final String TEMPLATE_EMAIL_VALID_CREDIT_NOTE = "valid-credit-note";

    public static final String TEMPLATE_EMAIL_CREDIT_NOTE = "credit-note";

    public static final String TEMPLATE_EMAIL_ACCEPT_COUPON = "accept-coupon";

    public static final String TEMPLATE_EMAIL_MODIFY_ORDER = "modify-order";

    public static final String TEMPLATE_EMAIL_CANCEL_ORDER = "cancel-order";

    public static final String TEMPLATE_EMAIL_CANCEL_MULTI_ORDER = "cancel-multi-order";

    public static final String TEMPLATE_EMAIL_NEW_INVOICE = "new-invoice";

    public static final String TEMPLATE_EMAIL_EXPORT_ORDER_EXCEL = "order-export-excel";

    public static final String TEMPLATE_EMAIL_EXPORT_COUPON_EXCEL = "coupon-export-excel";

    public static final String TEMPLATE_EMAIL_NEW_RECEIVED = "new-received";

    public static final String TEMPLATE_EMAIL_NEW_FACTURE = "new-facture";
    public static final String SUBJECT_PASSWORD_RESET = "GulfCam Réinitialisation du mot de passe";

    public static final String TEMPLATE_PASSWORD_RESET = "email-password-reset";

    public static final String DEFAULT_SIZE_PAGINATION = "20";

    public static final String DEFAULT_TIME_ZONE = "Africa/Douala";



    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
