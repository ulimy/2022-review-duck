import { API_URI, PAGE_OPTION } from 'constant';

import {
  GetTemplatesResponse,
  GetTemplateResponse,
  CreateFormResponse,
  TemplateFilterType,
  CreateTemplateRequest,
  CreateTemplateResponse,
  UpdateTemplateRequest,
} from '../types/template';

import axiosInstance from './config/axiosInstance';

export const getTemplates = async (
  filter: TemplateFilterType,
  pageNumber: string,
  itemCount?: number,
): Promise<GetTemplatesResponse> => {
  const { data } = await axiosInstance.get(
    `${API_URI.TEMPLATE.GET_TEMPLATES}?page=${pageNumber}&size=${
      itemCount || PAGE_OPTION.TEMPLATE_ITEM_SIZE
    }&sort=${filter}`,
  );

  return data;
};

export const getTemplate = async (templateId: number): Promise<GetTemplateResponse> => {
  const { data } = await axiosInstance.get(API_URI.TEMPLATE.GET_TEMPLATE(templateId));

  return data;
};

export const createForm = async (templateId: number): Promise<CreateFormResponse> => {
  const { data } = await axiosInstance.post(API_URI.TEMPLATE.CREATE_FORM(templateId));

  return data;
};

export const createTemplate = async (
  query: CreateTemplateRequest,
): Promise<CreateTemplateResponse> => {
  const { data } = await axiosInstance.post(API_URI.TEMPLATE.CREATE_TEMPLATE, query);

  return data;
};

export const updateTemplate = async ({
  templateId,
  templateTitle,
  templateDescription,
  questions,
}: UpdateTemplateRequest): Promise<null> => {
  const { data } = await axiosInstance.put(API_URI.TEMPLATE.UPDATE_TEMPLATE(templateId), {
    templateTitle,
    templateDescription,
    questions,
  });

  return data;
};

export const deleteTemplate = async (templateId: number): Promise<null> => {
  const { data } = await axiosInstance.delete(API_URI.TEMPLATE.DELETE_TEMPLATE(templateId));

  return data;
};
