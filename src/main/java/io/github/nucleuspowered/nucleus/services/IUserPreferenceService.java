package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.api.service.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;

@ImplementedBy(UserPreferenceService.class)
public interface IUserPreferenceService extends NucleusUserPreferenceService {

}
