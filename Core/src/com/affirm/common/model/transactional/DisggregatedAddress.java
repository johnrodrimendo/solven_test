package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.AreaType;
import com.affirm.common.model.catalog.HouseType;
import com.affirm.common.model.catalog.StreetType;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 20/12/17.
 */
public class DisggregatedAddress {

    private Integer id;
    private char type;
    private Integer personId;
    private StreetType streetType;
    private String streetName;
    private String streetnumber;
    private String square;
    private String allotment;
    private HouseType houseType;
    private Integer interiorNumber;
    private AreaType areaType;
    private String zoneName;
    private String reference;
    private String block;
    private String kilometer;
    private String ward;
    private String stage;
    private String group;
    private String sector;
    private String parcel;
    private Ubigeo ubigeo;
    private String ineiUbigeo;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "disaggregated_address_id", null));
        setType(JsonUtil.getCharacterFromJson(json, "disaggregated_address_type", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setStreetType(catalog.getStreetType(JsonUtil.getIntFromJson(json, "street_type_id", null)));
        setStreetName(JsonUtil.getStringFromJson(json, "street_name", null));
        setStreetnumber(JsonUtil.getStringFromJson(json, "street_number", null));
        setSquare(JsonUtil.getStringFromJson(json, "square", null));
        setAllotment(JsonUtil.getStringFromJson(json, "allotment", null));
        setHouseType(catalog.getHouseTypeById(JsonUtil.getIntFromJson(json, "interior_type", null)));
        setInteriorNumber(JsonUtil.getIntFromJson(json, "interior_number", null));
        setAreaType(catalog.getAreaTypeById(JsonUtil.getIntFromJson(json, "zone_type", null)));
        setZoneName(JsonUtil.getStringFromJson(json, "zone_name", null));
        setReference(JsonUtil.getStringFromJson(json, "reference", null));
        setBlock(JsonUtil.getStringFromJson(json, "block", null));
        setKilometer(JsonUtil.getStringFromJson(json, "kilometer", null));
        setWard(JsonUtil.getStringFromJson(json, "ward", null));
        setStage(JsonUtil.getStringFromJson(json, "stage", null));
        setGroup(JsonUtil.getStringFromJson(json, "group", null));
        setSector(JsonUtil.getStringFromJson(json, "sector", null));
        setParcel(JsonUtil.getStringFromJson(json, "parcel", null));
        setUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        setIneiUbigeo(JsonUtil.getStringFromJson(json, "inei_ubigeo_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public StreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(StreetType streetType) {
        this.streetType = streetType;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }

    public String getAllotment() {
        return allotment;
    }

    public void setAllotment(String allotment) {
        this.allotment = allotment;
    }

    public Integer getInteriorNumber() {
        return interiorNumber;
    }

    public void setInteriorNumber(Integer interiorNumber) {
        this.interiorNumber = interiorNumber;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getParcel() {
        return parcel;
    }

    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getIneiUbigeo() {
        return ineiUbigeo;
    }

    public void setIneiUbigeo(String ineiUbigeo) {
        this.ineiUbigeo = ineiUbigeo;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getStreetnumber() {
        return streetnumber;
    }

    public void setStreetnumber(String streetnumber) {
        this.streetnumber = streetnumber;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }
}
