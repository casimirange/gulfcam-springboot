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

    public static final String SUBJECT_EMAIL_NEW_RECEIVED = "GulfCam - Reçue de paiement : #Ref";

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

    public static final String TEMPLATE_EMAIL_NEW_RECEIVED = "new-received";

    public static final String SUBJECT_EMAIL_PAY_SERVICE_ABONNEMENT = "nouveau membre";

    public static final String SUBJECT_EMAIL_INFO = "GulfCam - Votre accès temporaire";

    public static final String SUBJECT_EMAIL_INFO_SERVICE_ABONNEMENT = "Demande d'information";

    public static final String SUBJECT_EMAIL_VALIDE_PROFILE = "Demande de validation de profil d'un membre ";

    public static final String SUBJECT_EMAIL_ENTREPRISE = "GulfCam - Demande d'information";

    public static final String SUBJECT_EMAIL_ENTREPRISE_EMPLOYE = "GulfCam - Accès espace employé";

    public static final String SUBJECT_EMAIL_ENTREPRISE_MEMBRE = "GulfCam - Accès Espace entreprise";

    public static final String SUBJECT_EMAIL_NEW_ENTREPRISE = "Nouvelle entreprise";

    public static final String SUBJECT_PASSWORD_RESET = "GulfCam Réinitialisation du mot de passe";

    public static final String TEMPLATE_EMAIL_PROFIL_NOT_VALID = "profil-not-valide";

    public static final String TEMPLATE_EMAIL_SUPPORT = "support";

    public static final String TEMPLATE_EMAIL_ACCESS_MEMBRE = "membre-access";

    public static final String TEMPLATE_EMAIL_INFO_SERVICE_ABONNEMENT = "information-service-abonnement";

    public static final String TEMPLATE_EMAIL_INFO = "information";

    public static final String TEMPLATE_EMAIL_INFO_PAIEMENT= "information-payement";

    public static final String TEMPLATE_EMAIL_ENTREPRISE = "entreprise-access";

    public static final String TEMPLATE_EMAIL_ENTREPRISE_EMPLOYE = "employe-access";

    public static final String TEMPLATE_EMAIL_NEW_ENTREPRISE = "information-new-entreprise";

    public static final String TEMPLATE_PASSWORD_RESET = "email-password-reset";

    public static final String PRODUCER_EMAIL_ENTREPRISE = "producer.email.entreprise" ;

    public static final String PRODUCER_EMAIL_EMPLOYE = "producer.email.employe" ;


    public static final String PRODUCER_EMAIL_RESET_PASSWORD = "producer.email.reset.password" ;

    public static final String PRODUCER_SMS_OTP = "producer.sms.otp" ;

    public static final String PRODUCER_PUSH_NOTIFICATION = "producer.push.notification" ;

    public static final String DEFAULT_SIZE_PAGINATION = "20";

    public static final String DEFAULT_TIME_ZONE = "Africa/Douala";



    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
