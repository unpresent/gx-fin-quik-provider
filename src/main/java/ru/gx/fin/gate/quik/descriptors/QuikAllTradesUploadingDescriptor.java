package ru.gx.fin.gate.quik.descriptors;

import ru.gx.fin.gate.quik.model.internal.QuikAllTrade;
import ru.gx.fin.gate.quik.model.internal.QuikAllTradesPackage;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptor;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptorsDefaults;

public class QuikAllTradesUploadingDescriptor extends OutcomeTopicUploadingDescriptor<QuikAllTrade, QuikAllTradesPackage> {
    public QuikAllTradesUploadingDescriptor(String topic, OutcomeTopicUploadingDescriptorsDefaults defaults) {
        super(topic, defaults);
    }
}
