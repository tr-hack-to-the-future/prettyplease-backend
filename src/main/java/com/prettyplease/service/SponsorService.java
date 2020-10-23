package com.prettyplease.service;

import com.prettyplease.model.tables.Sponsor;

import java.util.List;

public interface SponsorService {

    List<Sponsor> getSponsors(String sponsorId);

}
