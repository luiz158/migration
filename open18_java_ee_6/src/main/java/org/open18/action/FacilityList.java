package org.open18.action;

//@Name("facilityList")
public class FacilityList {

//	@Logger
//	private Log log;
//
//	private static final String[] RESTRICTIONS = {
//			"lower(facility.address) like concat(lower(#{facilityList.facility.address}),'%')",
//			"lower(facility.city) like concat(lower(#{facilityList.facility.city}),'%')",
//			"lower(facility.country) like concat(lower(#{facilityList.facility.country}),'%')",
//			"lower(facility.county) like concat(lower(#{facilityList.facility.county}),'%')",
//			"lower(facility.description) like concat(lower(#{facilityList.facility.description}),'%')",
//			"lower(facility.name) like concat(lower(#{facilit>.List.facility.name}),'%')",
//			"lower(facility.phone) like concat(lower(#{facilityList.facility.phone}),'%')",
//			"lower(facility.state) like concat(lower(#{facilityList.facility.state}),'%')",
//			"lower(facility.type) like concat(lower(#{facilityList.facility.type}),'%')",
//			"lower(facility.uri) like concat(lower(#{facilityList.facility.uri}),'%')",
//			"lower(facility.zip) like concat(lower(#{facilityList.facility.zip}),'%')",};
//
//	private Facility facility = new Facility();
//
//	@Override
//	public String getEjbql() {
//		return "select facility from Facility facility";
//	}
//
//	@Override
//	public Integer getMaxResults() {
//		return 25;
//	}
//
//	@Override
//	public String getOrder() {
//		if (super.getOrder() == null) {
//			setOrder("name asc");
//		}
//		return super.getOrder();
//	}
//
//	public Facility getFacility() {
//		return facility;
//	}
//
//	@Override
//	public List<String> getRestrictions() {
//		return Arrays.asList(RESTRICTIONS);
//	}
//
//	public void preloadFacilities() {
//		log.debug("begin preloading facilities (current phase: " + FacesLifecycle.getPhaseId() + ")");
//		getResultList();
//		getResultCount();
//		log.debug("finished preloading facilities");
//	}

}
