package com.miracle.bandwidth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.bandwidth.bean.SprintTest;
import com.miracle.bandwidth.utility.BandwidthUtility;
import com.miracle.cognitive.global.bean.SprintResource;
import com.miracle.cognitive.global.bean.Velocity;
import com.miracle.common.response.FeatureResponse;
import com.miracle.database.bean.Release;
import com.miracle.database.bean.Resource;
import com.miracle.database.bean.Sprint;
import com.miracle.database.bean.Team;
import com.miracle.utility.DataUtility;

@RestController
public class BandwidthController {

	@Autowired
	private DataUtility dataUtility;
	@Autowired
	private BandwidthUtility bandwidthUtility;

	@GetMapping(value = "/bandwidth")
	public ResponseEntity<FeatureResponse> buildFeatures(@RequestParam(value = "version") double version) {
		FeatureResponse response = new FeatureResponse();
		try {
			Release release = dataUtility.loadRelease(version);
			if (release == null) {
				response.setObject("Invalid version provided");
				response.setSuccess(false);
				return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_REQUEST);
			}
			List<Sprint> sprintList = dataUtility.getSprintList();
			int duration = sprintList.get(0).getDuration();

			List<SprintTest> sprintDetails = bandwidthUtility.retrieveSprintDetails(release, duration);

			Team team = dataUtility.loadTeam(release.getTeamName());
			List<Resource> resourceList = dataUtility.getResourceList();
			List<SprintResource> resourceDataMap = bandwidthUtility.getResourceLeaves(team.getTeamName(), resourceList,
					sprintDetails);

			List<Velocity> velocityList = bandwidthUtility.calcVelocity(team, resourceDataMap);
			response.setObject(velocityList);
			response.setSuccess(true);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.OK);
		} catch (Exception exception) {
			exception.printStackTrace();
			response.setObject("Failed to calculate Bandwidth");
			response.setSuccess(false);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_GATEWAY);
		}
	}

}
