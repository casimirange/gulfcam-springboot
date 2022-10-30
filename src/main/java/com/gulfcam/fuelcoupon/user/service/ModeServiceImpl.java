package com.gulfcam.fuelcoupon.user.service;


import com.gulfcam.fuelcoupon.user.entity.Mode;
import com.gulfcam.fuelcoupon.user.repository.IModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ModeServiceImpl implements IModeService {

	@Autowired
	private IModeRepository modeRepo;

	@Override
	public Mode findById(Long id) {
		return modeRepo.findByModeIdAndIsDeleteNull(id)
				.orElseThrow(() -> new ResourceNotFoundException("Mode id " + id + " not found"));
	}

	@Override
	public Page<Mode> findAll() {
		return modeRepo.findByIsDeleteIsNull(PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "modeId")));
	}

	@Override
	@Transactional
	public <S extends Mode> S save(S entity) {
		return modeRepo.save(entity);
	}

	@Override
	@Transactional
	public Mode editMode(Mode mode, Mode newMode) {
		mode.setModeName(newMode.getModeName());
		mode.setModePrice(newMode.getModePrice());
		mode.setModeDescription(newMode.getModeDescription());
		return modeRepo.save(mode);
	}

	@Override
	@Transactional
	public Mode deleteMode(Long modeId) {
		Mode mode = findById(modeId);
		mode.setIsDelete(LocalDateTime.now());
		return modeRepo.save(mode);
	}

	@Override
	public boolean existsByName(String modeName, Long modeId) {
		Optional<Mode> mode = modeRepo.findByModeNameIgnoreCaseAndIsDeleteNull(modeName);
		return checkOwnerIdentity(modeId, mode);
	}

	private boolean checkOwnerIdentity(Long id, Optional<Mode> mode) {
		boolean taken = false;
		if (!mode.isPresent()) {
			return taken;
		}
		if (id != null) {
			if (id.equals(mode.get().getModeId())) {
				return taken;
			} else {
				taken = true;
				return taken;
			}
		} else {
			taken = true;
			return taken;
		}
	}
}
