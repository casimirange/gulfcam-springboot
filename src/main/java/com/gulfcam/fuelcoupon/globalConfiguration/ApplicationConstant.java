package com.gulfcam.fuelcoupon.globalConfiguration;

import javax.servlet.http.HttpServletRequest;

public class ApplicationConstant {

    public static final String ENTREPRISE_NAME = "GULFCAM";

    public static final String SUBJECT_EMAIL_PAY = "GulfCam - Accès espace membres";

    public static final String SUBJECT_EMAIL_OPT = "GulfCam - Code OTP";

    public static final String SUBJECT_EMAIL_NEW_USER = "GulfCam - Bienvenue";

    public static final String SUBJECT_EMAIL_PAY_TEMPORAIRE = "GulfCam - Accès Temporaire";

    public static final String SUBJECT_EMAIL_VILID_PROFIL = "GulfCam - Validation de profil ";

    public static final String SUBJECT_EMAIL_VILID_PROFIL_NOT_VALIDE = "GulfCam - Profil non valide";

    public static final String TEMPLATE_EMAIL_ENTREPRISE_MEMBRE = "email-verification";

    public static final String TEMPLATE_EMAIL_NEW_USER = "new-user";

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
