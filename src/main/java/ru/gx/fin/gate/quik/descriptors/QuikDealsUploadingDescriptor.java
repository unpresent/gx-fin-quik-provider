package ru.gx.fin.gate.quik.descriptors;

import ru.gx.fin.gate.quik.model.internal.QuikDeal;
import ru.gx.fin.gate.quik.model.internal.QuikDealsPackage;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptor;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptorsDefaults;

public class QuikDealsUploadingDescriptor extends OutcomeTopicUploadingDescriptor<QuikDeal, QuikDealsPackage> {
    public QuikDealsUploadingDescriptor(String topic, OutcomeTopicUploadingDescriptorsDefaults defaults) {
        super(topic, defaults);
    }
}
