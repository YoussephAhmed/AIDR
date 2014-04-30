package qa.qcri.aidr.predictui.api;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import qa.qcri.aidr.predictui.entities.Crisis;
import qa.qcri.aidr.predictui.entities.Document;
import qa.qcri.aidr.predictui.entities.ModelFamily;
import qa.qcri.aidr.predictui.facade.CrisisResourceFacade;
import qa.qcri.aidr.predictui.facade.DocumentFacade;
import qa.qcri.aidr.predictui.facade.ModelFamilyFacade;

@Path("/manage/collection")
@Stateless
public class CrisisManagementResource {
	
	@Context
    private UriInfo context;
    @EJB
    private DocumentFacade documentLocalEJB;
    
    @EJB
    private CrisisResourceFacade crisisLocalEJB;

    @EJB
    private ModelFamilyFacade modelFamilyLocalEJB;
    
    @PersistenceContext(unitName = "qa.qcri.aidr.predictui-EJBS")
    private EntityManager em;

    public CrisisManagementResource() {
    }
    
	@GET
	@Path("/trash/crisis/{crisisCode}")
	@Produces({"application/json"})
	public Response trashByCrisisCode(@PathParam("crisisCode") String crisisCode) {
		//TODO: 
		// 1. set aidr_predict.crisis.isTrashed = true
		// 2. set model_family.isActive = 0
		// 3. remove tasks for this crisisID from document table -->
		//    this will trigger deletion of documents for this crisisID from 
		//    the task_assignment table through DELETE CASCADE
		System.out.println("Received request to trash collection: " + crisisCode);
		Crisis crisis = crisisLocalEJB.getCrisisByCode(crisisCode);
		crisis.setIsTrashed(true);
		em.merge(crisis);
		
		List<ModelFamily> associatedModels = modelFamilyLocalEJB.getAllModelFamiliesByCrisis(crisis.getCrisisID());
		for (ModelFamily model: associatedModels) {
			model.setIsActive(false);
			em.merge(model);
		}
		
		List<Document> associatedDocs = documentLocalEJB.getAllUnlabeledDocumentbyCrisisID(crisis);
		System.out.println("Found for " + crisisCode + ", unlabeled docs  = " + associatedDocs.size());
		for (Document document: associatedDocs) {
			em.remove(document);
		}
		List<Document> temp = documentLocalEJB.getAllUnlabeledDocumentbyCrisisID(crisis);
		System.out.println("Post Trashing: found for " + crisisCode + ", unlabeled docs  = " + temp.size());		
		
		if (temp.isEmpty()) {
			StringBuilder sb = new StringBuilder().append("{\"TRASHED\":").append(crisis.getCrisisID());
			return Response.ok(sb.toString()).build();
		} else {
			return Response.ok("{\"status\": \"FAILED\"}").build();
		}
	}
	
	@GET
	@Path("/untrash/crisis/{crisisCode}")
	@Produces({"application/json"})
	public Response untrashByCrisisCode(@PathParam("crisisCode") String crisisCode) {
		//TODO: 
		// 1. set aidr_predict.crisis.isTrashed = false
		// 2. set model_family.isActive = 1
		System.out.println("Received request to trash collection: " + crisisCode);
		Crisis crisis = crisisLocalEJB.getCrisisByCode(crisisCode);
		crisis.setIsTrashed(false);
		em.merge(crisis);
		
		List<ModelFamily> associatedModels = modelFamilyLocalEJB.getAllModelFamiliesByCrisis(crisis.getCrisisID());
		for (ModelFamily model: associatedModels) {
			model.setIsActive(true);
			em.merge(model);
		}
		System.out.println("Found for " + crisisCode + ", model families  = " + associatedModels.size());
		return Response.ok("{\"status\": \"UNTRASHED\"}").build();
	}
	
}
