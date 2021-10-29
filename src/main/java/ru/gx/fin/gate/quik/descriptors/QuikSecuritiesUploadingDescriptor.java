package ru.gx.fin.gate.quik.descriptors;

import ru.gx.fin.gate.quik.model.internal.QuikSecuritiesPackage;
import ru.gx.fin.gate.quik.model.internal.QuikSecurity;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptor;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptorsDefaults;

public class QuikSecuritiesUploadingDescriptor extends OutcomeTopicUploadingDescriptor<QuikSecurity, QuikSecuritiesPackage> {
    public QuikSecuritiesUploadingDescriptor(String topic, OutcomeTopicUploadingDescriptorsDefaults defaults) {
        super(topic, defaults);
    }
}
