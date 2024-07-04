import com.epam.jmp.service.api.Service;
import com.epam.jmp.service.cloud.impl.CloudService;

module jmp.cloud.service.impl {
    requires transitive jmp.service.api;
    requires jmp.dto;
    exports com.epam.jmp.service.cloud.impl;
    provides Service with CloudService;
}