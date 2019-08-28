/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.api.service.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.UserPreferenceService;

@ImplementedBy(UserPreferenceService.class)
public interface IUserPreferenceService extends NucleusUserPreferenceService {

}