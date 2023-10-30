package com.shiep.service.impl;

import com.shiep.entity.PhotoAnimalSearch;
import com.shiep.mapper.IPhotoAnimalAdoptMapper;
import com.shiep.mapper.IPhotoAnimalSearchMapper;
import com.shiep.service.IUploadPhotoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shiep.entity.PhotoAnimalAdopt;

import javax.annotation.Resource;

@Service
public class UploadPhotoServiceImpl implements IUploadPhotoService {
    @Resource
    private IPhotoAnimalAdoptMapper photoAnimalAdopt;

    @Resource
    private IPhotoAnimalSearchMapper photoAnimalSearchMapper;

    @Transactional
    @Override
    public boolean saveAdoptPhoto(PhotoAnimalAdopt adopt) {
        if (adopt == null) {
            return false;
        }
        return this.photoAnimalAdopt.insert(adopt) > 0;
    }

    @Override
    public boolean saveSearchPhoto(PhotoAnimalSearch search) {
        if (search == null) {
            return false;
        }
        return this.photoAnimalSearchMapper.insert(search) > 0;
    }
}
