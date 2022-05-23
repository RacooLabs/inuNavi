package com.maru.inunavi.ui.map;


import com.maru.inunavi.R;

public class FindImage {


    int mainImageId = 0;

    public int getPlaceImageId(String placeCode){

        switch(placeCode){

            //식사
            case "FOODXIA" :

                mainImageId = R.drawable.place_foodxia_main;

                break;

            case "FOODSTU0" :

                mainImageId = R.drawable.place_foodstu0_main;

                break;

            case "FOODFHO" :

                mainImageId = R.drawable.place_foodfho_main;

                break;

            case "FOODSAL" :

                mainImageId = R.drawable.place_foodsal_main;

                break;

            case "FOODDUP" :

                mainImageId = R.drawable.place_fooddup_main;

                break;

            case "FOODGON" :

                mainImageId = R.drawable.place_foodgon_main;

                break;

            case "FOODTOM" :

                mainImageId = R.drawable.place_foodtom_main;

                break;

            case "FOODMEL" :

                mainImageId = R.drawable.place_foodmel_main;

                break;

            case "FOODGOG" :

                mainImageId = R.drawable.place_foodgog_main;

                break;

            case "FOODSTU1" :

                mainImageId = R.drawable.place_foodstu1_main;

                break;

            case "FOODSTU2" :

                mainImageId = R.drawable.place_foodstu2_main;

                break;

            case "FOODMOM" :

                mainImageId = R.drawable.place_foodmom_main;

                break;


            case "FOODPAL" :

                mainImageId = R.drawable.place_foodpal_main;

                break;

            case "FOODPRO" :

                mainImageId = R.drawable.place_foodpro_main;

                break;

            case "FOODCHD":

                mainImageId = R.drawable.place_foodchd_main;


            //편의점

            case "CONGSNAT" :

                mainImageId = R.drawable.place_congsnat_mini;

                break;

            case "CONGSENG" :

                mainImageId = R.drawable.place_congseng_mini;

                break;

            case "CONGSWEL" :

                mainImageId = R.drawable.place_congswel_mini;

                break;

            case "CONGSSCL" :

                mainImageId = R.drawable.place_congsscl_mini;

                break;

            case "CONGSLBR" :

                mainImageId = R.drawable.place_congslbr_mini;

                break;

            case "CONGSBIO" :

                mainImageId = R.drawable.place_congsbio_mini;

                break;

            case "CONEMSTU3" :

                mainImageId = R.drawable.place_conemstu3_mini;

                break;

            case "CONCUSTU2" :

                mainImageId = R.drawable.place_concustu2_mini;

                break;

            case "CONGSSTU1" :

                mainImageId = R.drawable.place_congsstu1_mini;

                break;

            case "CONEMLIB":

                mainImageId = R.drawable.place_conemlib_mini;



            //카페
            case "CAFECDLIB" :

                mainImageId = R.drawable.place_cafecdlib_mini;

                break;

            case "CAFECDOUT" :

                mainImageId = R.drawable.place_cafecdout_mini;

                break;

            case "CAFECDSTU0" :

                mainImageId = R.drawable.place_cafecdstu0_mini;

                break;

            case "CAFESNB" :

                mainImageId = R.drawable.place_cafect_mini;

                break;

            case "CAFEJC" :

                mainImageId = R.drawable.place_cafejc_mini;

                break;

            case "CAFEGZ" :

                mainImageId = R.drawable.place_cafegz_mini;

                break;

            case "CAFEMD" :

                mainImageId = R.drawable.place_cafemd_mini;

                break;

            case "CAFEHY" :

                mainImageId = R.drawable.place_cafehy_mini;

                break;
            case "CAFEPJS" :

                mainImageId = R.drawable.place_cafepjs_mini;

                break;

            case "CAFEPG" :

                mainImageId = R.drawable.place_cafepg_mini;

                break;

            case "CAFEJJCF" :

                mainImageId = R.drawable.place_cafejjcf_mini;

                break;

                



            //편의시설

            case "FACSLB":

                mainImageId = R.drawable.place_facslb_mini;

                break;

            case "FACSBATMWEL":

                mainImageId = R.drawable.place_facsbatmwel_mini;

                break;

            case "FACSB":

                mainImageId = R.drawable.place_facsb_mini;

                break;

            case "FACPOST":

                mainImageId = R.drawable.place_facpost_mini;

                break;

            case "FACRCV":

                mainImageId = R.drawable.place_facrcv_mini;

                break;

            case "FACTEL":

                mainImageId = R.drawable.place_factel_mini;

                break;

            case "FACGLS":

                mainImageId = R.drawable.place_facgls_mini;

                break;

            case "FACPHR":

                mainImageId = R.drawable.place_facphr_mini;

                break;

            case "FACHAIR":

                mainImageId = R.drawable.place_fachair_mini;

                break;

            case "FACCOPY":

                mainImageId = R.drawable.place_faccopy_mini;

                break;

            case "FACGOODS":

                mainImageId = R.drawable.place_facgoods_mini;

                break;

            case "FACSUR":

                mainImageId = R.drawable.place_facsur_mini;

                break;

            case "FACLIB":

                mainImageId = R.drawable.place_faclib_mini;

                break;

            case "FACSBATM27":

                mainImageId = R.drawable.place_facsbatm27_mini;

                break;

            case "FACJOB":

                mainImageId = R.drawable.place_facjob_mini;

                break;

            case "FACSTUCT":
            case "FACCSLSTU1":

                mainImageId = R.drawable.place_facstuct_mini;

                break;


            case "FACCSL28":


                mainImageId = R.drawable.place_faccsl28_mini;

                break;


            case "FACMEDIC":

                mainImageId = R.drawable.place_facmedic_mini;

                break;

            case "FACDREAM":

                mainImageId = R.drawable.place_facdream_mini;

                break;

            case "FACPACK":

                mainImageId = R.drawable.place_facpack_mini;

                break;


            // 부속건물

            case "ICE":

                mainImageId = R.drawable.place_bd_ice;

                break;

            case "IPR":

                mainImageId = R.drawable.place_bd_ipr;

                break;

            case "IAD":

                mainImageId = R.drawable.place_bd_iad;

                break;

            case "SC":

                mainImageId = R.drawable.place_bd_sc;

                break;

            case "SF":

                mainImageId = R.drawable.place_bd_sf;

                break;

            case "ILB":

                mainImageId = R.drawable.place_bd_ilb;

                break;

            case "SH":

                mainImageId = R.drawable.place_bd_sh;

                break;

            case "SI":

                mainImageId = R.drawable.place_bd_si;

                break;

            case "SJ":

                mainImageId = R.drawable.place_bd_sj;

                break;

            case "IGH":

                mainImageId = R.drawable.place_bd_igh;

                break;

            case "IWL":

                mainImageId = R.drawable.place_bd_iwl;

                break;

            case "SM":

                mainImageId = R.drawable.place_bd_sm;

                break;

            case "SN":

                mainImageId = R.drawable.place_bd_sn;

                break;

            case "SO":

                mainImageId = R.drawable.place_bd_so;

                break;

            case "SP":

                mainImageId = R.drawable.place_bd_sp;

                break;

            case "SQ":

                mainImageId = R.drawable.place_bd_sq;

                break;

            case "ISC":

                mainImageId = R.drawable.place_bd_isc;

                break;

            case "IST1":

                mainImageId = R.drawable.place_bd_ist1;

                break;

            case "IST2":

                mainImageId = R.drawable.place_bd_ist2;

                break;

            case "IST3":

                mainImageId = R.drawable.place_bd_ist3;

                break;

            case "ST":

                mainImageId = R.drawable.place_bd_st;

                break;

            case "SU":

                mainImageId = R.drawable.place_bd_su;

                break;

            case "SW":

                mainImageId = R.drawable.place_bd_sw;

                break;

            case "SX":

                mainImageId = R.drawable.place_bd_sx;

                break;


            case "ITP":

                mainImageId = R.drawable.place_bd_itp;

                break;


            case "IKD":

                mainImageId = R.drawable.place_bd_ikd;

                break;


            case "IWA":

                mainImageId = R.drawable.place_bd_iwa;

                break;


            case "SY1":

                mainImageId = R.drawable.place_bd_sy1;

                break;


            case "SY2":

                mainImageId = R.drawable.place_bd_sy2;

                break;

            case "SY3":

                mainImageId = R.drawable.place_bd_sy3;

                break;


            case "ZB":

                mainImageId = R.drawable.place_bd_zb;

                break;

            case "ZC":

                mainImageId = R.drawable.place_bd_zc;

                break;



            // 흡연실

            case "SMK5":

                mainImageId = R.drawable.place_smk5;

                break;

            case "SMK8":

                mainImageId = R.drawable.place_smk8;

                break;

            case "SMK12":

                mainImageId = R.drawable.place_smk12;

                break;

            case "SMK13":

                mainImageId = R.drawable.place_smk13;

                break;

            case "SMK14":

                mainImageId = R.drawable.place_smk14;

                break;

            case "SMK15":

                mainImageId = R.drawable.place_smk15;

                break;

            case "SMK16":

                mainImageId = R.drawable.place_smk16;

                break;

            case "SMK17":

                mainImageId = R.drawable.place_smk17;

                break;

            case "SMKSTU2":

                mainImageId = R.drawable.place_smkstu2;

                break;

            case "SMK28":

                mainImageId = R.drawable.place_smk28;

                break;

            case "SMK29":

                mainImageId = R.drawable.place_smk29;

                break;


            // 야외시설

            case "OFGF":

                mainImageId = R.drawable.place_ofgf;

                break;

            case "OFSC":

                mainImageId = R.drawable.place_ofsc;

                break;

            case "OFBK16":

                mainImageId = R.drawable.place_ofbk16;

                break;

            case "OFBK8":

                mainImageId = R.drawable.place_ofbk8;

                break;


            default:

                mainImageId = R.drawable.place_none;

                break;


        }

        return mainImageId;

    }

}